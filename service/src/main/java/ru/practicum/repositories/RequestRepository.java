package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.events.states.RequestState;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Integer countParticipationRequestsByEventAndStatus(Long id, RequestState state);

    List<ParticipationRequest> findParticipationRequestsByRequester(Long userId);

    List<ParticipationRequest> findParticipationRequestsByStatus(RequestState state);

    List<ParticipationRequest> findParticipationRequestsByEvent(Long eventId);

}
