package ru.yandex.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.exceptions.ForbiddenOperationException;
import ru.yandex.practicum.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.request.RequestMapper;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.dto.RequestStatus;
import ru.yandex.practicum.request.model.ParticipationRequest;
import ru.yandex.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class RequestService {
    private final RequestRepository repository;
    private final RequestMapper requestMapper;
    private final EventService eventService;

    public ParticipationRequestDto createRequest(Integer userId, Integer eventId) {
        if (repository.findByRequesterAndEvent(userId, eventId) != null) {
            throw new ForbiddenOperationException("User has already created the request", new Throwable("Integrity constraint has been violated."));
        }
        Event event = eventService.getEventByIdForCompilation(eventId);
        if (event.getInitiator().getId().equals(userId) || !event.getState().equals(State.PUBLISHED)
                || (event.getConfirmedRequests().equals(event.getParticipantLimit()) && event.getParticipantLimit() != 0)) {
            log.info("Forbidden, userId = {}, eventId = {} ", userId, eventId);
            throw new ForbiddenOperationException("Forbidden", new Throwable("Integrity constraint has been violated."));
        }
        ParticipationRequest newRequest = new ParticipationRequest();
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequester(userId);
        newRequest.setEvent(eventId);
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
            eventService.cancelOrConfirmRequest(eventId, true);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        return requestMapper.toParticipationRequestDto(repository.save(newRequest));
    }

    public List<ParticipationRequestDto> getRequestsByUserId(Integer userId) {
        return requestMapper.toParticipationRequestDto(repository.findAllByRequester(userId));
    }

    public List<ParticipationRequestDto> getRequestsByEventId(Integer eventId) {
        return requestMapper.toParticipationRequestDto(repository.findAllByEvent(eventId));
    }

    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        Optional<ParticipationRequest> requestOptional = repository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new ObjectNotFoundException("Request with id=" + requestId + " was not found",
                    new Throwable("The required object was not found."));
        } else {
            ParticipationRequest request = requestOptional.get();
            if (!request.getRequester().equals(userId)) {
                throw new ForbiddenOperationException("Request with id=" + requestId + " was not found",
                        new Throwable("The required object was not found."));
            } else {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    eventService.cancelOrConfirmRequest(request.getEvent(), false);
                }
                request.setStatus(RequestStatus.CANCELED);
                request = repository.save(request);
                return requestMapper.toParticipationRequestDto(request);
            }
        }
    }

    public EventRequestStatusUpdateResult updateRequestsStatus(Integer userId,
                                                               Integer eventId,
                                                               EventRequestStatusUpdateRequest requests) {
        Event event = eventService.getEventByIdForCompilation(eventId);
        if (event == null) {
            log.info("Event with id={} was not found", eventId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                    new Throwable("The required object was not found."));
        }
        if (requests.getStatus().equals(RequestStatus.CONFIRMED)) { // Если нужно подтвердить заявки
            if (event.getConfirmedRequests().equals(event.getParticipantLimit()) && event.getParticipantLimit() != 0) {
                log.info("The participant limit has been reached, eventId={} ", eventId);
                throw new ForbiddenOperationException("The participant limit has been reached",
                        new Throwable("For the requested operation the conditions are not met."));
            }
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            List<ParticipationRequestDto> confirmed = new ArrayList<>();
            List<ParticipationRequestDto> rejected = new ArrayList<>();
            boolean isLimit = false;
            List<Integer> ids = requests.getRequestIds();
            for (Integer id : ids) {
                Optional<ParticipationRequest> requestOptional = repository.findById(id);
                if (requestOptional.isEmpty()) {
                    throw new ObjectNotFoundException("Request with id=" + id + " was not found",
                            new Throwable("The required object was not found."));
                } else {
                    ParticipationRequest request = requestOptional.get();
                    if (!isLimit) {
                        if (request.getStatus().equals(RequestStatus.PENDING)) {
                            request.setStatus(RequestStatus.CONFIRMED);
                            eventService.cancelOrConfirmRequest(eventId, true);
                            request = repository.save(request);
                            confirmed.add(requestMapper.toParticipationRequestDto(request));

                            event = eventService.getEventByIdForCompilation(eventId);
                            if (event.getConfirmedRequests().equals(event.getParticipantLimit()) && event.getParticipantLimit() != 0) {
                                isLimit = true;
                            }
                        } else {
                            throw new ForbiddenOperationException("Status is incorrect",
                                    new Throwable("For the requested operation the conditions are not met."));
                        }
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        rejected.add(requestMapper.toParticipationRequestDto(request));
                    }
                }
            }
            result.setConfirmedRequests(confirmed);
            result.setRejectedRequests(rejected);
            return result;
        } else { // Если нужно отклонить заявки
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            List<ParticipationRequestDto> confirmed = new ArrayList<>();
            List<ParticipationRequestDto> rejected = new ArrayList<>();
            List<Integer> ids = requests.getRequestIds();
            for (Integer id : ids) {
                Optional<ParticipationRequest> requestOptional = repository.findById(id);
                if (requestOptional.isEmpty()) {
                    throw new ObjectNotFoundException("Request with id=" + id + " was not found",
                            new Throwable("The required object was not found."));
                } else {
                    ParticipationRequest request = requestOptional.get();
                    if (request.getStatus().equals(RequestStatus.PENDING)) {
                        request.setStatus(RequestStatus.REJECTED);
                        request = repository.save(request);
                        rejected.add(requestMapper.toParticipationRequestDto(request));
                    } else {
                        throw new ForbiddenOperationException("Status is incorrect",
                                new Throwable("For the requested operation the conditions are not met."));
                    }

                }
            }
            result.setConfirmedRequests(confirmed);
            result.setRejectedRequests(rejected);
            return result;
        }
    }
}
