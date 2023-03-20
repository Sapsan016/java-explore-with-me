package ru.practicum.private_service;

import ru.practicum.dto.events.EventFullDto;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.model.Event;

import java.util.Collection;
import java.util.List;

public interface PrivateService {
    Event addEvent(NewEventDto newEventDto, Long userId);

    List<Event> getEventsByUserId(Long userId, Integer from, Integer size);

    Event getFullEvent(Long userId, Long eventId);
}
