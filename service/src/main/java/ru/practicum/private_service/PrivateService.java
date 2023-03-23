package ru.practicum.private_service;

import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

public interface PrivateService {
    Event addEvent(NewEventDto newEventDto, Long userId);

    List<Event> getEventsByUserId(Long userId, Integer from, Integer size);

    Event getFullEvent(Long userId, Long eventId);

    Event updateEvent(UpdateEventRequest newEventDto, Long userId, Long eventId);

    Event findEventById(Long eventId);

    ParticipationRequest addRequest(Long userId, Long eventId);

    List<ParticipationRequest> getUserRequests(Long userId);

    ParticipationRequest cancelRequest(Long userId, Long requestId);

    EventRequestStatusUpdateResult updateRequest(EventRequestStatusUpdateRequest updateRequest,
                                                 Long userId, Long eventId);
}
