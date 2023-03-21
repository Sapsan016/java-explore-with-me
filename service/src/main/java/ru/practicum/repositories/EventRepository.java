package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT * FROM events WHERE initiator_id=? offset ? LIMIT ?", nativeQuery = true)
    List<Event> getAllEventsByUserId(Long userid, Integer from, Integer size);

    //    List<Event> findEventsByUserAndStateAndEventDateBetween(User user, EventState state, LocalDateTime rangeStart,
//                                                            LocalDateTime rangeEnd);
    @Query(value = "SELECT * FROM events WHERE initiator_id IN (SELECT unnest(array[?])) AND state IN (SELECT unnest(array[?])) " +
            "AND category_id IN " +
            "(SELECT unnest(array[?])) AND event_date >= ? AND event_date < ? offset ? LIMIT ?", nativeQuery = true)
    List<Event> findEventsByUserIdAndStateAndEventDateBetween(Long[] users, String[] states, Long[] categories,
                                                              LocalDateTime rangeStart,
                                                              LocalDateTime rangeEnd, Integer from, Integer size);

}
