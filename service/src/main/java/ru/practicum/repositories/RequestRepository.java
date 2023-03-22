package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ParticipationRequest;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Integer countParticipationRequestsByEvent(Long id);
}
