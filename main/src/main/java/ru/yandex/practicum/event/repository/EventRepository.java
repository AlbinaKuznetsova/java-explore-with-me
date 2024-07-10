package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findAllByInitiatorId(Integer userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Integer id, Integer initiatorId);

    Optional<Event> findByIdAndState(Integer id, State state);

    Page<Event> findAllByEventDateBetween(LocalDateTime rangeStartDate, LocalDateTime rangeEndDate, Pageable pageable);

    Page<Event> findAllByCategoryIdIn(Integer[] categories, Pageable pageable);

    Page<Event> findAllByCategoryIdInAndEventDateBetween(Integer[] categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByStateIn(State[] states, Pageable pageable);

    Page<Event> findAllByStateInAndEventDateBetween(State[] states, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByCategoryIdInAndStateIn(Integer[] categories, State[] states, Pageable pageable);

    Page<Event> findAllByCategoryIdInAndStateInAndEventDateBetween(Integer[] categories, State[] states, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByInitiatorIdIn(Integer[] users, Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndEventDateBetween(Integer[] users, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndCategoryIdIn(Integer[] users, Integer[] categories, Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndCategoryIdInAndEventDateBetween(Integer[] users, Integer[] categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndStateIn(Integer[] users, State[] states, Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndStateInAndEventDateBetween(Integer[] users, State[] states, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndCategoryIdInAndStateInAndEventDateBetween(Integer[] users, Integer[] categories,
                                                                                   State[] states, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Page<Event> findAllByInitiatorIdInAndCategoryIdInAndStateIn(Integer[] users, Integer[] categories,
                                                                State[] states, Pageable pageable);


    @Query(" select ev from Event as ev " +
            "where ev.eventDate between ?3 and ?4 " +
            "AND (lower(ev.annotation) LIKE lower(concat('%', ?1,'%')) OR lower(ev.description) LIKE lower(concat('%', ?1,'%'))) " +
            "AND ev.paid = ?2 " +
            "AND ev.confirmedRequests <> ev.participantLimit ")
    Page<Event> findAllByParamsAvailable(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Pageable pageable);

    @Query(" select ev from Event as ev " +
            "where ev.eventDate between ?4 and ?5 " +
            "AND (lower(ev.annotation) LIKE lower(concat('%', ?1,'%')) OR lower(ev.description) LIKE lower(concat('%', ?1,'%'))) " +
            "AND ev.category.id IN (?2) " +
            "AND ev.paid = ?3 " +
            "AND ev.confirmedRequests <> ev.participantLimit")
    Page<Event> findAllByParamsAvailable(String text, Integer[] categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Pageable pageable);

    @Query(" select ev from Event as ev " +
            "where ev.eventDate between ?1 and ?2 " +
            "AND ev.confirmedRequests <> ev.participantLimit ")
    Page<Event> findAllByParamsAvailable(LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query(" select ev from Event as ev " +
            "where ev.eventDate between ?4 and ?5 " +
            "AND (lower(ev.annotation) LIKE lower(concat('%', ?1,'%')) OR lower(ev.description) LIKE lower(concat('%', ?1,'%'))) " +
            "AND ev.category.id IN (?2) " +
            "AND ev.paid = ?3 ")
    Page<Event> findAllByParamsNotAvailable(String text, Integer[] categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Pageable pageable);

    @Query(" select ev from Event as ev " +
            "where ev.eventDate between ?3 and ?4 " +
            "AND (lower(ev.annotation) LIKE lower(concat('%', ?1,'%')) OR lower(ev.description) LIKE lower(concat('%', ?1,'%'))) " +
            "AND ev.paid = ?2 ")
    Page<Event> findAllByParamsNotAvailable(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Pageable pageable);

    @Query(" select ev from Event as ev " +
            "where ev.eventDate between ?1 and ?2 " +
            "AND ev.category.id IN (?3) ")
    Page<Event> findAllByCategoryIdIn(LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer[] categories, Pageable pageable);
}
