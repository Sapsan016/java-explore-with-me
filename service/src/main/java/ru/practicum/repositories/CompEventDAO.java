package ru.practicum.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CompEventDAO {
    private final JdbcTemplate jdbcTemplate;

    public void addNewCompEventPair(Long compId, Long eventId) {
        String sqlQuery = "insert into compilation_events (compilation_id, event_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, compId, eventId);
    }

    public void removeCompEvents(Long compId) {
        String sqlQuery = "delete from compilation_events where compilation_id = ?";
        jdbcTemplate.update(sqlQuery, compId);
    }

    public List<Long> getAllEventsId(Long compId) {
        String sqlQuery = "select event_id from compilation_events where compilation_id = ?";
        return jdbcTemplate.query(sqlQuery, CompEventDAO::mapRowToId, compId);
    }
    public static Long mapRowToId(ResultSet resultSet, int rowNum) throws SQLException {
        return (resultSet.getLong("event_id"));

    }
}
