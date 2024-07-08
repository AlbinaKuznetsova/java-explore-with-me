package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.request.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findAllByRequester(Integer userId);

    List<ParticipationRequest> findAllByEvent(Integer eventId);

    ParticipationRequest findByRequesterAndEvent(Integer userId, Integer eventId);
}
