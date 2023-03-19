package ru.practicum.dto.event;

import ru.practicum.model.Event;

public class EventMapper {
    public static Event toEvent(AddEventDto addEventDto) {
        return new Event(
                null,
                addEventDto.getName(),
                addEventDto.getEmail()
        );
    }

    public static EventDto toEventDto(Event event) {
        return new EventDto(
                event.getEmail(),
                event.getId(),
                event.getName()
        );
    }
}
