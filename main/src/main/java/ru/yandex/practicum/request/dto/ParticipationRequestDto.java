package ru.yandex.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Integer id;
    private LocalDateTime created = LocalDateTime.now();
    private Integer event;
    private Integer requester;
    private RequestStatus status;
}
