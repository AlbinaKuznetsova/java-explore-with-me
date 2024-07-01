package ru.yandex.practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestForStatDto {
    private String app;
    private String uri;
    private Long hits;
}
