package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.events.states.RequestState;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Integer countParticipationRequestsByEventAndStatus(Long id, RequestState state);

    List<ParticipationRequest> findParticipationRequestsByRequester(Long userId);

    List<ParticipationRequest> findParticipationRequestsByStatus(RequestState state);

    List<ParticipationRequest> findParticipationRequestsByEvent(Long eventId);
    @Query("select e from ParticipationRequest e " +
            "where e.status='PENDING' and e.id= ?1")
    ParticipationRequest findParticipationRequestsByIdAndStatus(Long requestId);

}
