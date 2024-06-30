package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.RequestDto;
import ru.yandex.practicum.RequestForStatDto;
import ru.yandex.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatService service;
    @PostMapping("/hit")
    public ResponseEntity<RequestDto> createRequest(@RequestBody RequestDto requestDto) {
        return ResponseEntity.ok().body(service.createRequest(requestDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<RequestForStatDto>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) String[] uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        return ResponseEntity.ok().body(service.getStats(start, end, uris, unique));
    }
}
