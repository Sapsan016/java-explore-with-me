package ru.practicum.private_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.events.requests.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.requests.EventRequestStatusUpdateResult;
import ru.practicum.dto.events.requests.ParticipationRequestDto;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.dto.events.states.EventActionStates;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.states.EventState;
import ru.practicum.dto.events.states.RequestState;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;
import ru.practicum.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


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
        if (newEventDto.getStateAction().equals(EventActionStates.SEND_TO_REVIEW))
            eventToUpdate.setState(EventState.PENDING);

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
        log.info("Выполняются проверки запроса от пользователя Id = {} на участие в событии Id = {}", userId, eventId);
        List<ParticipationRequest> userRequests = requestRepository.findParticipationRequestsByRequester(userId);
        if (userRequests.stream().anyMatch(request -> request.getEvent().equals(eventId)))
            throw new IllegalArgumentException(String.format("The request for participation by user in event with " +
                    "id=%s was already added", eventId));
        Event requestedEvent = findEventById(eventId);
        if (requestedEvent.getUser().getId().equals(userId))
            throw new IllegalArgumentException("The requester can't participate in his own event.");
        if (requestedEvent.getState().equals(EventState.PENDING))
            throw new IllegalArgumentException("The event is not published.");
        if (requestedEvent.getParticipantLimit() != 0 && checkRequestsCount(requestedEvent)) {
            log.error("The event's participation limit has been reached.");
            throw new IllegalArgumentException("The event's participation limit has been reached.");
        }

        ParticipationRequest requestToAdd = new ParticipationRequest(null, LocalDateTime.now(), eventId,
                userId, null);

        if (!requestedEvent.getRequestModeration() || requestedEvent.getParticipantLimit() == 0) {
            requestToAdd.setStatus(RequestState.CONFIRMED);
            requestRepository.save(requestToAdd);
            requestedEvent.setConfirmedRequests((requestedEvent.getConfirmedRequests() + 1));
            log.info("Добавлен подтвержденный запрос с Id = {} на участие в событии с Id = {}",
                    requestToAdd.getId(), eventId);
            return requestToAdd;
        }


        requestToAdd.setStatus(RequestState.PENDING);
        requestRepository.save(requestToAdd);
        log.info("Добавлен запрос с Id = {} на участие в событии с Id = {} от пользователя с Id = {}",
                requestToAdd.getId(), eventId, userId);
        return requestToAdd;
    }

    @Override
    public List<ParticipationRequest> getUserRequests(Long userId) {
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

    @Override
    public EventRequestStatusUpdateResult updateRequest(EventRequestStatusUpdateRequest updateRequest,
                                                        Long userId, Long eventId) {
        Event requestedEvent = findEventById(eventId);
        if (requestedEvent.getRequestModeration().equals(false) || requestedEvent.getParticipantLimit() == 0) {
            log.info("Для запросов на участи в событии Id = {} модерация запросов на участие не требуется", eventId);
            updateRequestsWithoutModeration(requestedEvent, updateRequest);
            return returnRequestUpdateResult();
        }

        List<ParticipationRequest> requestsToConfirm = requestRepository.findAllById(updateRequest.getRequestIds());

        for (int i = 0; i < requestsToConfirm.size(); i++) {
            ParticipationRequest request = requestsToConfirm.get(i);
            if (!request.getStatus().equals(RequestState.PENDING))
                throw new DataIntegrityViolationException("The request was already confirmed or canceled");
            if (checkRequestsCount(requestedEvent))
                throw new IllegalArgumentException("The event's participation limit has been reached.");
            request.setStatus(updateRequest.getStatus());
            log.info("Запрос на участие с Id = {} подтвержден", request.getId());
            requestedEvent.setConfirmedRequests((requestedEvent.getConfirmedRequests() + 1));
            requestRepository.save(request);

            if (checkRequestsCount(requestedEvent)) {
                for (int j = i + 1; j < requestsToConfirm.size(); j++) {
                    ParticipationRequest rejectedRequest = requestsToConfirm.get(j);
                    rejectedRequest.setStatus(RequestState.REJECTED);
                }
            }
        }
        requestRepository.saveAll(requestsToConfirm);
        eventRepository.save(requestedEvent);

        return returnRequestUpdateResult();
    }

    private EventRequestStatusUpdateResult returnRequestUpdateResult() {
        List<ParticipationRequestDto> confirmedRequests;
        List<ParticipationRequestDto> rejectedRequests;
        confirmedRequests = requestRepository
                .findParticipationRequestsByStatus(RequestState.CONFIRMED).stream()
                .map(RequestMapper::toDto)
                .sorted(Comparator.comparing(ParticipationRequestDto::getId).reversed())
                .collect(Collectors.toList());
        rejectedRequests = requestRepository
                .findParticipationRequestsByStatus(RequestState.REJECTED).stream()
                .map(RequestMapper::toDto)
                .sorted(Comparator.comparing(ParticipationRequestDto::getId).reversed())
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<ParticipationRequest> getUserEventRequests(Long userId, Long eventId) {
        log.info("Выполняется поиск запросов, на участие в событии с Id = {} ", eventId);
        return requestRepository.findParticipationRequestsByEvent(eventId);
    }


    private boolean checkRequestsCount(Event requestedEvent) {
        Integer requestCount = requestRepository.countParticipationRequestsByEventAndStatus(requestedEvent.getId(),
                RequestState.CONFIRMED);
        return requestCount >= (requestedEvent.getParticipantLimit());
    }

    private void updateRequestsWithoutModeration(Event event, EventRequestStatusUpdateRequest updateRequest) {
        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        requests.stream()
                .filter(request -> request.getStatus().equals(RequestState.PENDING))
                .forEach(request -> request.setStatus(RequestState.CONFIRMED));
        requestRepository.saveAll(requests);
        log.info("Для запросов c Id: {} установлен статус CONFIRMED", requests);

        event.setConfirmedRequests((event.getConfirmedRequests() +
                updateRequest.getRequestIds().size()));
        eventRepository.save(event);
        log.info("Для события c Id = {} добавлено {} подтвержденных запросов", event.getId(),
                updateRequest.getRequestIds().size());
    }

}
