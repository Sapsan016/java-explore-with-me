package ru.practicum.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompEventDAO {
    private final JdbcTemplate jdbcTemplate;

    public void addNewCompEventPair(Long compId, Long eventId) {
        String sqlQuery = "insert into compilation_events (compilation_id, event_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, compId, eventId);
    }

}
