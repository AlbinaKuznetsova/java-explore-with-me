package ru.yandex.practicum.request;

import org.mapstruct.Mapper;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

    List<ParticipationRequestDto> toParticipationRequestDto(List<ParticipationRequest> participationRequest);

    ParticipationRequest toParticipationRequest(ParticipationRequestDto participationRequestDto);
}
