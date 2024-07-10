package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findAllByRequesterId(Integer userId);

    List<ParticipationRequest> findAllByEventId(Integer eventId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Integer userId, Integer eventId);
}
