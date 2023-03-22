package ru.practicum.private_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.dto.events.states.EventActionStates;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.states.EventState;
import ru.practicum.dto.events.states.RequestState;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.*;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;
import ru.practicum.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PrivateServiceImpl implements PrivateService {

    EventRepository eventRepository;
    LocationRepository locationRepository;
    AdminService adminService;

    RequestRepository requestRepository;

    public PrivateServiceImpl(EventRepository eventRepository, LocationRepository locationRepository,
                              AdminService adminService, RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.adminService = adminService;
        this.requestRepository = requestRepository;
    }

    @Override
    public Event addEvent(NewEventDto newEventDto, Long userId) {
        User initiator = adminService.findUserById(userId);
        Category category = adminService.findCategoryById(newEventDto.getCategory());
        Event eventToAdd = EventMapper.toEvent(newEventDto);
        eventToAdd.setCategory(category);
        eventToAdd.setUser(initiator);
        locationRepository.save(eventToAdd.getLocation());
        log.info("Добавлена локация с Id = {}", eventToAdd.getLocation().getId());
        eventRepository.save(eventToAdd);
        log.info("Добавлено событие с Id = {}", eventToAdd.getId());
        return eventToAdd;
    }

    @Override
    public List<Event> getEventsByUserId(Long userId, Integer from, Integer size) {
        log.info("Выполняется поиск всех событий, добавленных пользователем с id = {} пропуская первых {}, " +
                "размер списка {}", userId, from, size);
        return eventRepository.getAllEventsByUserId(userId, from, size);
    }

    @Override
    public Event getFullEvent(Long userId, Long eventId) {
        log.info("Выполняется поиск полной информации о событии с Id = {}, добавленныом пользователем с id = {}",
                userId, eventId);
        adminService.findUserById(userId);
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId)));
    }

    @Override
    public Event updateEvent(UpdateEventRequest newEventDto, Long userId, Long eventId) {
        Event eventToUpdate = findEventById(eventId);

        eventToUpdate = adminService.checkUpdateEvent(eventToUpdate, newEventDto);

        if (newEventDto.getStateAction().equals(EventActionStates.CANCEL_REVIEW))
            eventToUpdate.setState(EventState.CANCELED);

        eventRepository.save(eventToUpdate);
        log.info("Обновлено событие с Id = {}", eventToUpdate.getId());
        return eventToUpdate;
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId)));
    }

    @Override
    public ParticipationRequest addRequest(Long userId, Long eventId) {
        Event requestedEvent = findEventById(eventId);
        log.info("Выполняются проверки запроса от пользователя Id = {} на участие в событии Id = {}", userId, eventId);
        if (requestedEvent.getUser().getId().equals(userId))
            throw new IllegalArgumentException("The requester can't participate in his own event.");
        if (requestedEvent.getState().equals(EventState.PENDING))
            throw new IllegalArgumentException("The event is not published.");
        if (requestedEvent.getParticipantLimit() != 0) {
            Integer requestCount = requestRepository.countParticipationRequestsByEvent(eventId);
            if (requestCount.equals(requestedEvent.getParticipantLimit()))
                throw new IllegalArgumentException("The event's participation limit has reached.");
        }
        ParticipationRequest requestToAdd = new ParticipationRequest(null, LocalDateTime.now(), eventId,
                userId, null);
        if (!requestedEvent.getRequestModeration()) {
            requestToAdd.setStatus(RequestState.CONFIRMED);
            requestRepository.save(requestToAdd);
            log.info("Добавлен запрос с Id = {} на участие в событии с Id = {}", requestToAdd.getId(), eventId);
            return requestToAdd;
        }
        requestToAdd.setStatus(RequestState.PENDING);
        requestRepository.save(requestToAdd);
        log.info("Добавлен запрос с Id = {} на участие в событии с Id = {}", requestToAdd.getId(), eventId);
        return requestToAdd;
    }

    @Override
    public List<ParticipationRequest> getUserRequests(Long userId) {
        adminService.findUserById(userId);
        log.info("Выполняется поиск запросов пользователя с Id = {}", userId);
        return requestRepository.findParticipationRequestsByRequester(userId);
    }

    @Override
    public ParticipationRequest cancelRequest(Long userId, Long requestId) {
        adminService.findUserById(userId);
        ParticipationRequest requestToCancel = requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%s was not found", requestId)));
        requestToCancel.setStatus(RequestState.CANCELED);
        requestRepository.save(requestToCancel);
        return requestToCancel;
    }
}
