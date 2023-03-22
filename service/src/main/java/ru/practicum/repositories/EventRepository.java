package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

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






}
