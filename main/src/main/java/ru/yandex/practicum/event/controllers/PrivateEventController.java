package ru.yandex.practicum.event.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.UpdateEventUserRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                                    @PathVariable Integer userId) {
        return new ResponseEntity<EventFullDto>(eventService.createEvent(newEventDto, userId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByUserId(
            @PathVariable Integer userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(eventService.getEventsByUserId(userId, from, size));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(
            @PathVariable Integer userId,
            @PathVariable Integer eventId) {
        return ResponseEntity.ok().body(eventService.getEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @RequestBody @Valid UpdateEventUserRequest newEvent) {
        return ResponseEntity.ok().body(eventService.updateEvent(userId, eventId, newEvent));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(
            @PathVariable Integer userId,
            @PathVariable Integer eventId) {
        return ResponseEntity.ok().body(requestService.getRequestsByEventId(eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestsStatus(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @RequestBody EventRequestStatusUpdateRequest requests) {
        return ResponseEntity.ok().body(requestService.updateRequestsStatus(userId, eventId, requests));
    }
}