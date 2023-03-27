package ru.practicum.private_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.events.EventFullDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.likes.LikeDto;
import ru.practicum.dto.events.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.events.requests.ParticipationRequestDto;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.LikeMapper;
import ru.practicum.mappers.RequestMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class PrivateController {

    private final PrivateService privateService;

    public PrivateController(PrivateService privateService) {
        this.privateService = privateService;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@RequestBody @Valid NewEventDto newEventDto, @PathVariable Long userId) {
        log.info("PublicController: Получен запрос на добавление события {} от пользователя ID = {}",
                newEventDto.toString(), userId);
        return EventMapper.toEventFullDto(privateService.addEvent(newEventDto, userId));
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicController: Получен запрос на поиск событий, добавленных пользователем с ID = {}," +
                "пропуская первых {}, размер списка = {}", userId, from, size);
        return privateService.getEventsByUserId(userId, from, size)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getFullEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("PublicController: Получен запрос на поиск полной информации о событии c ID = {} " +
                "от пользователя Id = {}", eventId, userId);
        return EventMapper.toEventFullDto(privateService.getFullEvent(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Valid UpdateEventRequest updateEventDto, @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("PublicController: Получен запрос на обновления события {} от пользователя ID = {}",
                updateEventDto, userId);
        return EventMapper.toEventFullDto(privateService.updateEvent(updateEventDto, userId, eventId));
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @RequestParam Long eventId) {
        log.info("PublicController: Получен новый запрос на участие в событии ID = {} от пользователя ID = {}",
                eventId, userId);
        return RequestMapper.toDto(privateService.addRequest(userId, eventId));
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequest(@PathVariable Long userId) {
        log.info("PublicController: Получен поиск запросов, добавленных пользователем с ID = {}", userId);
        return privateService.getUserRequests(userId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("PublicController: Получено удаление поиск запроса с ID = {}, от пользователя с ID = {}",
                requestId, userId);
        return RequestMapper.toDto(privateService.cancelRequest(userId, requestId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@RequestBody EventRequestStatusUpdateRequest updateRequest,
                                                        @PathVariable Long userId,
                                                        @PathVariable Long eventId) {
        log.info("PublicController: Получено обновление статуса запросов {} на участие в событии с ID = {}, " +
                "добавленном пользователем с Id = {}", updateRequest, eventId, userId);
        return privateService.updateRequest(updateRequest, userId, eventId);

    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequest(@PathVariable Long userId,
                                                             @PathVariable Long eventId) {
        log.info("PublicController: Получен поиск информации о запросах на участие " +
                "в событии с ID = {}, добавленном пользователем ID = {}", eventId, userId);
        return privateService.getUserEventRequests(userId, eventId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto addLike(@PathVariable Long userId,
                           @PathVariable Long eventId,
                           @RequestParam(defaultValue = "true") Boolean like) {
        log.info("PublicController: Получен запрос на добавление лайлка {} событию ID = {} " +
                "от пользователя ID = {}", like, eventId, userId);
        return LikeMapper.toDto(privateService.addLike(userId, eventId, like));
    }

}
