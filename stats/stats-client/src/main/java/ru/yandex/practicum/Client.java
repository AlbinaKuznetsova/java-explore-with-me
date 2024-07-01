package ru.yandex.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class Client {
    protected final RestTemplate rest;
    private static final String serverUrl = "http://localhost:9090";

    @Autowired
    public Client(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }


    public ResponseEntity<RequestDto> createRequest(RequestDto requestDto) {
        HttpEntity requestEntity = new HttpEntity<>(requestDto, defaultHeaders());

        return rest.exchange("/hit", HttpMethod.POST, requestEntity, RequestDto.class);
    }

    public ResponseEntity<List<RequestForStatDto>> getStats(
            LocalDateTime start,
            LocalDateTime end,
            String[] uris,
            boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        HttpEntity requestEntity = new HttpEntity<>(defaultHeaders());

        return rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<RequestForStatDto>>() {
        }, parameters);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

}
