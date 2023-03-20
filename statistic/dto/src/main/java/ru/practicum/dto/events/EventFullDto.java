package ru.practicum.dto.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.users.UserShortDto;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    String annotation;

    CatDto category;

    Integer confirmedRequests;

    String createdOn;

    String description;

    String eventDate;

    Long id;

    UserShortDto initiator;

    Location location;

    Boolean paid;

    Integer participantLimit;

    LocalDateTime publishedOn;

    Boolean requestModeration;

    EventState state;

    String title;

    Integer views;
}
