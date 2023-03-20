package ru.practicum.private_service;

import ru.practicum.dto.events.NewEventDto;
import ru.practicum.model.Event;

public interface PrivateService {
    Event addEvent(NewEventDto newEventDto, Long userId);
}
