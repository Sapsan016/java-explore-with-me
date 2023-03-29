package ru.practicum.dto.events.likes;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.users.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikesDto {

    Long id;

    EventShortDto event;

    UserShortDto user;

    Boolean Like;

}
