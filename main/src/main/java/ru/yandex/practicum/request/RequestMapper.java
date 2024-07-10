package ru.yandex.practicum.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "event", source = "participationRequest.event.id")
    @Mapping(target = "requester", source = "participationRequest.requester.id")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

    List<ParticipationRequestDto> toParticipationRequestDto(List<ParticipationRequest> participationRequest);

    @Mapping(target = "event.id", source = "participationRequestDto.event")
    @Mapping(target = "requester.id", source = "participationRequestDto.requester")
    ParticipationRequest toParticipationRequest(ParticipationRequestDto participationRequestDto);
}
