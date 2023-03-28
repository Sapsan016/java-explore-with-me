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
import ru.practicum.repositories.LikesRepository;
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
    LikesRepository likesRepository;

    public PrivateServiceImpl(EventRepository eventRepository, LocationRepository locationRepository,
                              AdminService adminService, RequestRepository requestRepository, LikesRepository likesRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.adminService = adminService;
        this.requestRepository = requestRepository;
        this.likesRepository = likesRepository;
    }

    @Override
    public Event addEvent(NewEventDto newEventDto, Long userId) {
        User initiator = adminService.findUserById(userId);
        Category category = adminService.findCategoryById(newEventDto.getCategory());
        Event eventToAdd = EventMapper.toEvent(newEventDto);
        eventToAdd.setCategory(category);
        eventToAdd.setUser(initiator);
        locationRepository.save(eventToAdd.getLocation());
        log.info("Добавлена локация с ID = {}", eventToAdd.getLocation().getId());
        eventRepository.save(eventToAdd);
        log.info("Добавлено событие с ID = {}", eventToAdd.getId());
        return eventToAdd;
    }

    @Override
    public List<Event> getEventsByUserId(Long userId, Integer from, Integer size) {
        log.info("Выполняется поиск всех событий, добавленных пользователем с ID = {} пропуская первых {}, " +
                "размер списка {}", userId, from, size);
        return eventRepository.getAllEventsByUserId(userId, from, size);
    }

    @Override
    public Event getFullEvent(Long userId, Long eventId) {
        log.info("Выполняется поиск полной информации о событии с ID = {}, добавленныом пользователем с ID = {}",
                userId, eventId);
        adminService.findUserById(userId);
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Событие с ID=%s не найдено", eventId)));
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
        log.info("Обновлено событие с ID = {}", eventToUpdate.getId());
        return eventToUpdate;
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Событие с ID=%s не найдено", eventId)));
    }

    @Override
    public ParticipationRequest addRequest(Long userId, Long eventId) {
        log.info("Выполняются проверки запроса от пользователя ID = {} на участие в событии ID = {}", userId, eventId);
        List<ParticipationRequest> userRequests = requestRepository.findParticipationRequestsByRequester(userId);
        if (userRequests.stream().anyMatch(request -> request.getEvent().equals(eventId)))
            throw new IllegalArgumentException(String.format("Запрос на участие в событии с ID=%s уже добавлен.",
                    eventId));
        Event requestedEvent = findEventById(eventId);
        if (requestedEvent.getUser().getId().equals(userId))
            throw new IllegalArgumentException("Нельзя самому участвовать в добавленном Вами событии.");
        if (requestedEvent.getState().equals(EventState.PENDING))
            throw new IllegalArgumentException("Событие не опубликовано.");
        if (requestedEvent.getParticipantLimit() != 0 && checkRequestsCount(requestedEvent)) {
            log.error("Достигнут лимит на количество участников события.");
            throw new IllegalArgumentException("Достигнут лимит на количество участников события.");
        }

        ParticipationRequest requestToAdd = new ParticipationRequest(null, LocalDateTime.now(), eventId,
                userId, null);

        if (!requestedEvent.getRequestModeration() || requestedEvent.getParticipantLimit() == 0) {
            requestToAdd.setStatus(RequestState.CONFIRMED);
            requestRepository.save(requestToAdd);
            requestedEvent.setConfirmedRequests((requestedEvent.getConfirmedRequests() + 1));
            log.info("Добавлен подтвержденный запрос с ID = {} на участие в событии с Id = {}",
                    requestToAdd.getId(), eventId);
            return requestToAdd;
        }


        requestToAdd.setStatus(RequestState.PENDING);
        requestRepository.save(requestToAdd);
        log.info("Добавлен запрос с ID = {} на участие в событии с ID = {} от пользователя с ID = {}",
                requestToAdd.getId(), eventId, userId);
        return requestToAdd;
    }

    @Override
    public List<ParticipationRequest> getUserRequests(Long userId) {
        log.info("Выполняется поиск запросов пользователя с ID = {}", userId);
        return requestRepository.findParticipationRequestsByRequester(userId);
    }

    @Override
    public ParticipationRequest cancelRequest(Long userId, Long requestId) {
        adminService.findUserById(userId);
        ParticipationRequest requestToCancel = requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Событие с ID=%s не найдено", requestId)));
        requestToCancel.setStatus(RequestState.CANCELED);
        requestRepository.save(requestToCancel);
        return requestToCancel;
    }

    @Override
    public EventRequestStatusUpdateResult updateRequest(EventRequestStatusUpdateRequest updateRequest,
                                                        Long userId, Long eventId) {
        Event requestedEvent = findEventById(eventId);
        if (requestedEvent.getRequestModeration().equals(false) || requestedEvent.getParticipantLimit() == 0) {
            log.info("Для запросов на участи в событии ID = {} модерация запросов на участие не требуется", eventId);
            updateRequestsWithoutModeration(requestedEvent, updateRequest);
            return returnRequestUpdateResult();
        }

        List<ParticipationRequest> requestsToConfirm = requestRepository.findAllById(updateRequest.getRequestIds());

        for (int i = 0; i < requestsToConfirm.size(); i++) {
            ParticipationRequest request = requestsToConfirm.get(i);
            if (!request.getStatus().equals(RequestState.PENDING))
                throw new DataIntegrityViolationException("Запрос уже подтвержден или отменен");
            if (checkRequestsCount(requestedEvent))
                throw new IllegalArgumentException("Достигнут лимит на количество участников события.");
            request.setStatus(updateRequest.getStatus());
            log.info("Запрос на участие с ID = {} подтвержден", request.getId());
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

    @Override
    public Like addLike(Long userId, Long eventId, Boolean like) {
        Event eventToLike = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Событие с ID=%s не найдено", eventId)));
        if (!eventToLike.getState().equals(EventState.PUBLISHED))
            throw new IllegalArgumentException("Событие не опубликовано.");
        User userLike = adminService.findUserById(userId);
        if (likesRepository.findByEventAndUser(eventToLike, userLike) != null)
            throw new IllegalArgumentException("Пользователь уже поставил лайк этому событию");

        Like likeToAdd = new Like(null, eventToLike, userLike, like);
        likesRepository.save(likeToAdd);
        log.info("Добавлен лайк с ID = {}, пользователем с ID = {} событию с ID = {}", likeToAdd.getId(),
                userId, eventId);
        Integer rate = likesRepository.countByIsLikeIsTrueAndEvent(eventToLike) -
                likesRepository.countByIsLikeIsFalseAndEvent(eventToLike);

       eventToLike.setRate(rate);
       eventRepository.save(eventToLike);
        log.info("У события с ID = {} установлен рейтинг:{}", eventId, rate);
        return likeToAdd;
    }

    @Override
    public Like changeLike(Long userId, Long likeId, Boolean like) {
        Like likeToChange = likesRepository.findById(likeId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Событие с ID=%s не найдено", likeId)));
        if (!likeToChange.getUser().getId().equals(userId))
            throw new IllegalArgumentException("Пользователь не ставил лайк этому событию");
        likeToChange.setIsLike(like);
        likesRepository.save(likeToChange);
        log.info("Изменен лайк с ID = {}, пользователем с ID = {}", likeId, userId);
        return likeToChange;
    }

    @Override
    public List<Event> getLikedEventsByUserId(Long userId, Integer from, Integer size) {
        User userLiked = adminService.findUserById(userId);
        List<Like> likes = likesRepository.findByUserAndIsLikeIsTrue(userLiked);
        List<Long> eventIds = likes.stream().map(like -> like.getEvent().getId()).collect(Collectors.toList());
        log.info("Выполняется поиск событий c ID: {} понравившихся пользователю с ID = {}, " +
                "пропуская первые: {} событий, размер списка:{}", eventIds, userId, from, size);
        return eventRepository.findAllById(eventIds).stream().skip(from).limit(size).collect(Collectors.toList());
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
        log.info("Для запросов c ID: {} установлен статус CONFIRMED", requests);

        event.setConfirmedRequests((event.getConfirmedRequests() +
                updateRequest.getRequestIds().size()));
        eventRepository.save(event);
        log.info("Для события c ID = {} добавлено {} подтвержденных запросов", event.getId(),
                updateRequest.getRequestIds().size());
    }

}
