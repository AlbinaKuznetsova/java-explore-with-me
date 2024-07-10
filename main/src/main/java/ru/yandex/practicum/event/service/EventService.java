package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.Client;
import ru.yandex.practicum.RequestDto;
import ru.yandex.practicum.category.CategoryMapper;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.event.EventMapper;
import ru.yandex.practicum.event.dto.*;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.LocationRepository;
import ru.yandex.practicum.exceptions.ForbiddenOperationException;
import ru.yandex.practicum.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
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
    private static final String APP_NAME = "ewm-main-service";
    private final Client statClient;
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
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));
        return eventMapper.toEventShortDto(eventRepository.findAllByInitiatorId(userId, pageable).toList());
    }

    public EventFullDto getEventById(Integer userId, Integer eventId) {
        return eventMapper.toEventFullDto(eventRepository
                .findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                        new Throwable("The required object was not found."))));
    }

    public EventFullDto getEventByIdWithStat(Integer eventId, HttpServletRequest request) {
        Integer hits = addToStat(request);
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                        new Throwable("The required object was not found.")));
        event.setViews(hits);
        eventRepository.save(event);
        return eventMapper.toEventFullDto(event);
    }

    public Event getEventByIdForCompilation(Integer eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                new Throwable("The required object was not found.")));
    }

    public List<EventFullDto> getEventsByParams(Integer[] users, State[] states, Integer[] categories, String rangeStart,
                                                String rangeEnd, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));
        LocalDateTime rangeStartDate = null;
        LocalDateTime rangeEndDate = null;
        if (rangeStart != null) {
            rangeStartDate = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeEnd != null) {
            rangeEndDate = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (users == null) {
            if (states == null) {
                if (categories == null) {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(eventRepository.findAll(pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(eventRepository
                                .findAllByEventDateBetween(rangeStartDate, rangeEndDate, pageable).toList());
                    }
                } else {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(eventRepository.findAllByCategoryIdIn(categories, pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(eventRepository
                                .findAllByCategoryIdInAndEventDateBetween(categories, rangeStartDate, rangeEndDate, pageable).toList());
                    }
                }
            } else {
                if (categories == null) {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(eventRepository.findAllByStateIn(states, pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(eventRepository
                                .findAllByStateInAndEventDateBetween(states, rangeStartDate, rangeEndDate, pageable).toList());
                    }
                } else {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(eventRepository.findAllByCategoryIdInAndStateIn(categories, states, pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(eventRepository
                                .findAllByCategoryIdInAndStateInAndEventDateBetween(categories, states, rangeStartDate, rangeEndDate, pageable).toList());
                    }
                }
            }
        } else {
            if (states == null) {
                if (categories == null) {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(eventRepository.findAllByInitiatorIdIn(users, pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(eventRepository
                                .findAllByInitiatorIdInAndEventDateBetween(users, rangeStartDate, rangeEndDate, pageable).toList());
                    }
                } else {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(eventRepository.findAllByInitiatorIdInAndCategoryIdIn(users, categories, pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(eventRepository
                                .findAllByInitiatorIdInAndCategoryIdInAndEventDateBetween(users, categories, rangeStartDate, rangeEndDate, pageable).toList());
                    }
                }
            } else {
                if (categories == null) {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(eventRepository.findAllByInitiatorIdInAndStateIn(users, states, pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(eventRepository
                                .findAllByInitiatorIdInAndStateInAndEventDateBetween(users, states, rangeStartDate, rangeEndDate, pageable).toList());
                    }
                } else {
                    if (rangeStartDate == null && rangeEndDate == null) {
                        return eventMapper.toEventFullDto(
                                eventRepository.findAllByInitiatorIdInAndCategoryIdInAndStateIn(
                                        users, categories, states, pageable).toList());
                    } else {
                        return eventMapper.toEventFullDto(
                                eventRepository.findAllByInitiatorIdInAndCategoryIdInAndStateInAndEventDateBetween(
                                        users, categories, states, rangeStartDate, rangeEndDate, pageable).toList());
                    }
                }
            }
        }
    }

    public List<EventShortDto> getEventsPublic(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        addToStat(request);
        if (sort == null) {
            sort = "id";
        } else if (sort.equals("EVENT_DATE")) {
            sort = "eventDate";
        } else if (sort.equals("VIEWS")) {
            sort = "views";
        }
        if (categories != null) {
            for (Integer idCat : categories) {
                if (idCat <= 0) {
                    throw new ValidationException("Parametr is not valid", new Throwable("Incorrectly made request."));
                }
            }
        }
        if (paid == null) {
            paid = false;
        }
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, sort));
        LocalDateTime rangeStartDate;
        LocalDateTime rangeEndDate = null;
        if (rangeStart != null) {
            rangeStartDate = LocalDateTime.parse(rangeStart, formatter);
        } else {
            rangeStartDate = LocalDateTime.now();
        }
        if (rangeEnd != null) {
            rangeEndDate = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            rangeStartDate = LocalDateTime.now().plusYears(100000);
        }
        if (text == null) {
            if (categories.length == 0) {
                if (onlyAvailable) {
                    return eventMapper.toEventShortDto(eventRepository.findAllByParamsAvailable(rangeStartDate,
                            rangeEndDate, pageable).toList());
                } else {
                    return eventMapper.toEventShortDto(eventRepository.findAllByEventDateBetween(rangeStartDate,
                            rangeEndDate, pageable).toList());
                }
            } else {
                if (onlyAvailable) {
                    return eventMapper.toEventShortDto(eventRepository.findAllByCategoryIdIn(rangeStartDate,
                            rangeEndDate, categories, pageable).toList());
                } else {
                    return eventMapper.toEventShortDto(eventRepository.findAllByCategoryIdIn(categories, pageable).toList());
                }
            }
        } else {
            if (categories.length == 0) {
                if (onlyAvailable) {
                    return eventMapper.toEventShortDto(eventRepository.findAllByParamsAvailable(text,
                            paid, rangeStartDate, rangeEndDate, pageable).toList());
                } else {
                    return eventMapper.toEventShortDto(eventRepository.findAllByParamsNotAvailable(text,
                            paid, rangeStartDate, rangeEndDate, pageable).toList());
                }
            } else {
                if (onlyAvailable) {
                    return eventMapper.toEventShortDto(eventRepository.findAllByParamsAvailable(text, categories,
                            paid, rangeStartDate, rangeEndDate, pageable).toList());
                } else {
                    return eventMapper.toEventShortDto(eventRepository.findAllByParamsNotAvailable(text, categories,
                            paid, rangeStartDate, rangeEndDate, pageable).toList());
                }
            }
        }
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
                if (newEvent.getPaid() != null) {
                    updatedEvent.setPaid(newEvent.getPaid());
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
                return eventMapper.toEventFullDto(eventRepository.save(updatedEvent));
            }
        } else {
            log.info("Event with id={} was not found", eventId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                    new Throwable("The required object was not found."));
        }
    }

    public EventFullDto updateEventByAdmin(Integer eventId, UpdateEventAdminRequest newEvent) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
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
                if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
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
            if (newEvent.getPaid() != null) {
                updatedEvent.setPaid(newEvent.getPaid());
            }
            if (newEvent.getStateAction() != null) {
                if (newEvent.getStateAction() == StateAction.PUBLISH_EVENT && updatedEvent.getState() == State.PENDING) {
                    updatedEvent.setState(State.PUBLISHED);
                    updatedEvent.setPublishedOn(LocalDateTime.now());
                } else if (newEvent.getStateAction() == StateAction.REJECT_EVENT && updatedEvent.getState() == State.PENDING) {
                    updatedEvent.setState(State.CANCELED);
                } else {
                    throw new ForbiddenOperationException("Only pending or canceled events can be changed",
                            new Throwable("Cannot publish or cancel the event because it's not in the right state"));
                }
            }
            return eventMapper.toEventFullDto(eventRepository.save(updatedEvent));
        } else {
            log.info("Event with id={} was not found", eventId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                    new Throwable("The required object was not found."));
        }
    }

    public void cancelOrConfirmRequest(Integer eventId, Boolean isConfirm) {
        if (eventRepository.findById(eventId).isEmpty()) {
            log.info("Event with id={} was not found", eventId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found",
                    new Throwable("The required object was not found."));
        } else {
            Event event = eventRepository.findById(eventId).get();
            if (isConfirm) {
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            }
            eventRepository.save(event);
        }
    }

    private Integer addToStat(HttpServletRequest request) {
        try {
            RequestDto statRequest = new RequestDto();
            statRequest.setTimestamp(LocalDateTime.now());
            statRequest.setApp(APP_NAME);
            statRequest.setIp(request.getRemoteAddr());
            statRequest.setUri(request.getRequestURI());
            statClient.createRequest(statRequest);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        // Записываем свежее значение в поле views
        String[] uris = {request.getRequestURI()};
        return Math.toIntExact(statClient.getStats(LocalDateTime.now().minusYears(2000).format(formatter), LocalDateTime.now().plusYears(100000).format(formatter), uris, true).getBody().get(0).getHits());

    }
}
