package ru.practicum.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {
    List<EndpointHit> findAllByTimestampBetweenAndUri(LocalDateTime start, LocalDateTime end, String uri);

    @Query(value = "SELECT DISTINCT ON (ip) hit_id, app, uri, ip, timestamp FROM hits WHERE uri=? " +
            "AND (timestamp between ? and ?)", nativeQuery = true)
    List<EndpointHit> findUniqueUriStats(String uri, LocalDateTime start, LocalDateTime end);
}
