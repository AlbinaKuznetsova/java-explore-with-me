package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.RequestDto;
import ru.yandex.practicum.RequestForStatDto;
import ru.yandex.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StatService {
    private final RequestRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RequestDto createRequest(RequestDto requestDto) {
        log.info("Create Request " + requestDto);
        return repository.save(requestDto);
    }

    public List<RequestForStatDto> getStats(String start,
                                            String end,
                                            String[] uris,
                                            boolean unique) {
        LocalDateTime newStart = LocalDateTime.parse(start, formatter);
        LocalDateTime newEnd = LocalDateTime.parse(end, formatter);
        if (newStart.isAfter(newEnd)) {
            throw new IllegalArgumentException("Incorrect dates");
        } else {
            if (unique) {
                if (uris == null) {
                    return repository.getStatUniqueIpWithoutUris(newStart, newEnd);
                } else {
                    return repository.getStatUniqueIp(newStart, newEnd, uris);
                }
            } else {
                if (uris == null) {
                    return repository.getStatWithoutUris(newStart, newEnd);
                } else {
                    return repository.getStat(newStart, newEnd, uris);
                }
            }
        }
    }
}
