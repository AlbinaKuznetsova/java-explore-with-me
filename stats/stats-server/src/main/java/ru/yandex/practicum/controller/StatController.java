package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.RequestDto;
import ru.yandex.practicum.RequestForStatDto;
import ru.yandex.practicum.service.StatService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatService service;

    @PostMapping("/hit")
    public ResponseEntity<RequestDto> createRequest(@RequestBody RequestDto requestDto) {
        return new ResponseEntity<RequestDto>(service.createRequest(requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<RequestForStatDto>> getStats(
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end,
            @RequestParam(name = "uris", required = false) String[] uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        return ResponseEntity.ok().body(service.getStats(start, end, uris, unique));
    }
}
