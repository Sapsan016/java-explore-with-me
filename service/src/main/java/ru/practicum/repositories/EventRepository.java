package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT * FROM events WHERE initiator_id = ? offset ? LIMIT ?", nativeQuery = true)
    List<Event> getAllEventsByUserId(Long userid, Integer from, Integer size);

    List<Event> findEventByState(String state);

    @Query(value = "SELECT * FROM events WHERE category_id = ? offset ? LIMIT ?", nativeQuery = true)
    List<Event> getEventsByCategoryId(Long categoryId, Integer from, Integer size);

    @Query(value = "SELECT * FROM events WHERE event_date between (? and ?) offset ? LIMIT ?", nativeQuery = true)
    List<Event> getEventsByTimeAndFromAndSize(LocalDateTime start, LocalDateTime end, Integer from, Integer size);

    @Query(value = "SELECT * FROM events WHERE event_date >= ? offset ? LIMIT ?", nativeQuery = true)
    List<Event> getEventsByStartTimeAndFromAndSize(LocalDateTime start, Integer from, Integer size);

    @Query(value = "SELECT * FROM events WHERE event_date <= ? offset ? LIMIT ?", nativeQuery = true)
    List<Event> getEventsByEndTimeAndFromAndSize(LocalDateTime end, Integer from, Integer size);

    @Query(value = "SELECT * FROM events offset ? LIMIT ?", nativeQuery = true)
    List<Event> getEventsByFromAndSize(Integer from, Integer size);

    @Query("select e from Event e " +
            "where e.state='PUBLISHED' and (lower(e.annotation) like lower(concat('%', ?1, '%')) " +
            " or lower(e.description) like lower(concat('%', ?1, '%'))) and e.eventDate>= ?2 and e.eventDate < ?3")
    List<Event> searchWithStartEnd(String text, LocalDateTime startRange, LocalDateTime endRange);
    @Query("select e from Event e " +
            "where e.state='PUBLISHED' and (lower(e.annotation) like lower(concat('%', ?1, '%')) " +
            " or lower(e.description) like lower(concat('%', ?1, '%'))) and e.eventDate>= ?2")
    List<Event> searchEventsAfterStartRange(String text, LocalDateTime startRange);

    @Query("select e from Event e " +
            "where e.state='PUBLISHED' and (lower(e.annotation) like lower(concat('%', ?1, '%')) " +
            " or lower(e.description) like lower(concat('%', ?1, '%'))) and e.eventDate< ?2")
    List<Event> searchEventsBeforeEndRange(String text, LocalDateTime endRange);






}
