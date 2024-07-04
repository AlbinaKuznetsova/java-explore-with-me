package ru.yandex.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query(value = "select * from events as e WHERE e.initiator_id = ?1" +
            " ORDER BY e.id LIMIT ?3 OFFSET ?2", nativeQuery = true)
    List<Event> findAllByUserIdFromSize(Integer userId, Integer from, Integer size);

    Event findByIdAndInitiatorId(Integer id, Integer initiatorId);
}
