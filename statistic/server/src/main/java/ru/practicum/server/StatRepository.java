package ru.practicum.server;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {
}
