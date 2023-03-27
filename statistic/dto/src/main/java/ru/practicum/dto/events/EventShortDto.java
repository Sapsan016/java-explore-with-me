package ru.practicum.dto.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.users.UserShortDto;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    String description;

    String annotation;

    CatDto category;

    Integer confirmedRequests;

    String eventDate;

    Long id;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Integer views;
}
