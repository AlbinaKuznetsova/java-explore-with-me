package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.CategoryMapper;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.event.EventMapper;
import ru.yandex.practicum.event.dto.*;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.LocationRepository;
import ru.yandex.practicum.exceptions.ForbiddenOperationException;
import ru.yandex.practicum.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventFullDto createEvent(NewEventDto newEventDto,
                                    Integer userId) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: " + newEventDto.getEventDate(),
                    new Throwable("For the requested operation the conditions are not met."));
        }
        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(userService.getUserById(userId));
        event.setState(State.PENDING);
        locationRepository.save(event.getLocation());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventShortDto> getEventsByUserId(Integer userId, Integer from, Integer size) {
        return eventMapper.toEventShortDto(eventRepository.findAllByUserIdFromSize(userId, from, size));
    }

    public EventFullDto getEventById(Integer userId, Integer eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId));
    }

    public EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventUserRequest newEvent) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            if (eventOpt.get().getState() == State.PUBLISHED) {
                throw new ForbiddenOperationException("Only pending or canceled events can be changed",
                        new Throwable("For the requested operation the conditions are not met."));
            } else {
                Event updatedEvent = eventOpt.get();
                if (newEvent.getAnnotation() != null) {
                    updatedEvent.setAnnotation(newEvent.getAnnotation());
                }
                if (newEvent.getCategory() != null) {
                    CategoryDto category = categoryService.getCategory(newEvent.getCategory());
                    updatedEvent.setCategory(categoryMapper.toCategory(category));
                }
                if (newEvent.getDescription() != null) {
                    updatedEvent.setDescription(newEvent.getDescription());
                }
                if (newEvent.getEventDate() != null) {
                    if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                        throw new IllegalArgumentException("Field: eventDate. Error: должно содержать дату, " +
                                "которая еще не наступила. Value: " + newEvent.getEventDate(),
                                new Throwable("For the requested operation the conditions are not met."));
                    } else {
                        updatedEvent.setEventDate(newEvent.getEventDate());
                    }
                }
                if (newEvent.getLocation() != null) {
                    locationRepository.save(newEvent.getLocation());
                    updatedEvent.setLocation(newEvent.getLocation());
                }
                if (newEvent.getParticipantLimit() != null) {
                    updatedEvent.setParticipantLimit(newEvent.getParticipantLimit());
                }
                if (newEvent.getRequestModeration() != null) {
                    updatedEvent.setRequestModeration(newEvent.getRequestModeration());
                }
                if (newEvent.getTitle() != null) {
                    updatedEvent.setTitle(newEvent.getTitle());
                }
                if (newEvent.getStateAction() != null) {
                    if (newEvent.getStateAction() == StateAction.CANCEL_REVIEW) {
                        updatedEvent.setState(State.CANCELED);
                    } else if (newEvent.getStateAction() == StateAction.SEND_TO_REVIEW) {
                        updatedEvent.setState(State.PENDING);
                    }
                }

                return eventMapper.toEventFullDto(updatedEvent);
            }
        } else {
            log.info("Category with id={} was not found", eventId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                    new Throwable("The required object was not found."));
        }
    }
}
