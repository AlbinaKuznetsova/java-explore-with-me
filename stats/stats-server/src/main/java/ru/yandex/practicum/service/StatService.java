package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.RequestDto;
import ru.yandex.practicum.RequestForStatDto;
import ru.yandex.practicum.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StatService {
    private final RequestRepository repository;

    public RequestDto createRequest(RequestDto requestDto) {
        log.info("Create Request " + requestDto);
        return repository.save(requestDto);
    }

    public List<RequestForStatDto> getStats(LocalDateTime start,
                                            LocalDateTime end,
                                            String[] uris,
                                            boolean unique) {
        if (unique) {
            if (uris == null) {
                return repository.getStatUniqueIpWithoutUris(start, end);
            } else {
                return repository.getStatUniqueIp(start, end, uris);
            }
        } else {
            if (uris == null) {
                return repository.getStatWithoutUris(start, end);
            } else {
                return repository.getStat(start, end, uris);
            }
        }
    }
}
