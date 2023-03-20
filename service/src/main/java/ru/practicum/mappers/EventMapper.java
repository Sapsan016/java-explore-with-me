package ru.practicum.mappers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CatMapper;
import ru.practicum.dto.events.EventFullDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.users.UserDto;
import ru.practicum.dto.users.UserMapper;
import ru.practicum.dto.users.UserShortDto;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@FieldDefaults(level = AccessLevel.PRIVATE)

public class EventMapper {

    static CategoryRepository categoryRepository;
    static UserRepository userRepository;


    static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEvent(NewEventDto newEventDto) {
        return new Event(
                null,
                newEventDto.getAnnotation(),
                newEventDto.getCategoryId(),
                null,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                null,
                newEventDto.getLocation(),
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.getRequestModeration(),
                EventState.PENDING,
                newEventDto.getTitle(),
                null
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                CatMapper.toCatDto(categoryRepository.findById(event.getCategoryId()).get()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(FORMATTER),
                event.getDescription(),
                event.getEventDate().format(FORMATTER),
                event.getId(),
                UserMapper.toUserShortDto(userRepository.findById(event.getUserId()).get()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(FORMATTER),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getDescription(),
                event.getAnnotation(),
                CatMapper.toCatDto(categoryRepository.findById(event.getCategoryId()).get()),
                event.getConfirmedRequests(),
                event.getEventDate().format(FORMATTER),
                event.getId(),
                UserMapper.toUserShortDto(userRepository.findById(event.getUserId()).get()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }
}


