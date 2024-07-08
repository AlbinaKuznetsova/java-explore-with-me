package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.event.dto.State;
import ru.yandex.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findAllByInitiatorId(Integer userId, Pageable pageable);

    Event findByIdAndInitiatorId(Integer id, Integer initiatorId);

    Event findByIdAndState(Integer id, State state);

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


    @Query(value = " select * from events as ev " +
            "where ev.event_date between ?3 and ?4 " +
            "AND (ev.annotation ILIKE %?1% OR ev.description ILIKE %?1%) " +
            "AND ev.paid = ?2 " +
            "AND ev.confirmed_requests <> ev.participant_limit " +
            "ORDER BY ?5 ASC LIMIT ?7 OFFSET ?6 ", nativeQuery = true)
    List<Event> findAllByParamsAvailable(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         String sort, Integer from, Integer size);

    @Query(value = " select * from events as ev " +
            "where ev.event_date between ?4 and ?5 " +
            "AND (ev.annotation ILIKE %?1% OR ev.description ILIKE %?1%) " +
            "AND ev.category_id IN (?2) " +
            "AND ev.paid = ?3 " +
            "AND ev.confirmed_requests <> ev.participant_limit " +
            "ORDER BY ?6 ASC LIMIT ?8 OFFSET ?7 ", nativeQuery = true)
    List<Event> findAllByParamsAvailable(String text, Integer[] categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         String sort, Integer from, Integer size);

    @Query(value = " select * from events as ev " +
            "where ev.event_date between ?1 and ?2 " +
            "AND ev.confirmed_requests <> ev.participant_limit " +
            "ORDER BY ?3 ASC LIMIT ?5 OFFSET ?4 ", nativeQuery = true)
    List<Event> findAllByParamsAvailable(LocalDateTime rangeStart, LocalDateTime rangeEnd, String sort, Integer from, Integer size);

    @Query(value = " select * from events as ev " +
            "where ev.event_date between ?4 and ?5 " +
            "AND (ev.annotation ILIKE %?1% OR ev.description ILIKE %?1%) " +
            "AND ev.category_id IN (?2) " +
            "AND ev.paid = ?3 " +
            "ORDER BY ?6 ASC LIMIT ?8 OFFSET ?7 ", nativeQuery = true)
    List<Event> findAllByParamsNotAvailable(String text, Integer[] categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            String sort, Integer from, Integer size);

    @Query(value = " select * from events as ev " +
            "where ev.event_date between ?3 and ?4 " +
            "AND (ev.annotation ILIKE %?1% OR ev.description ILIKE %?1%) " +
            "AND ev.paid = ?2 " +
            "ORDER BY ?5 ASC LIMIT ?7 OFFSET ?6 ", nativeQuery = true)
    List<Event> findAllByParamsNotAvailable(String text, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            String sort, Integer from, Integer size);

    @Query(value = " select * from events as ev " +
            "where ev.event_date between ?1 and ?2 " +
            "AND ev.category_id IN (?3) " +
            "ORDER BY ?4 ASC LIMIT ?6 OFFSET ?5 ", nativeQuery = true)
    List<Event> findAllByCategoryIdIn(LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer[] categories, String sort, Integer from, Integer size);
}
