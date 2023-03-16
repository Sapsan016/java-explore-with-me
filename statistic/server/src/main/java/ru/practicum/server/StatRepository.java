package ru.practicum.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {
    List<EndpointHit> findAllByTimestampBetween(String start, String end);
    List<EndpointHit> findAllByTimestampBetweenAndUri(String start, String end, String uri);





}
