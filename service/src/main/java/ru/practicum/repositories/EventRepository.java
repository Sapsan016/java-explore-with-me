package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository <Event, Long> {
    @Query(value = "SELECT * FROM events WHERE initiator_id=? offset ? LIMIT ?", nativeQuery = true)
    List<Event> getAllEventsByUserId(Long userid, Integer from, Integer size);
}
