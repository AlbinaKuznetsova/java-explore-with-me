package ru.yandex.practicum.event.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsPublic(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) Integer[] categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @PositiveOrZero @RequestParam(name = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {
        return ResponseEntity.ok().body(eventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventByIdWithStat(@PathVariable Integer id, HttpServletRequest request) {
        return ResponseEntity.ok().body(eventService.getEventByIdWithStat(id, request));
    }

}
