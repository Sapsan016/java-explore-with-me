package ru.practicum.private_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.events.EventActionState;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.UpdateEventUserRequest;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.*;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;

import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PrivateServiceImpl implements PrivateService {

    EventRepository eventRepository;
    LocationRepository locationRepository;
    AdminService adminService;

    public PrivateServiceImpl(EventRepository eventRepository, LocationRepository locationRepository,
                              AdminService adminService) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.adminService = adminService;
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
    public Event updateEvent(UpdateEventUserRequest newEventDto, Long userId, Long eventId) {
        Event eventToUpdate = findEventById(eventId);
        if (eventToUpdate.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("Only pending or canceled events can be changed");
        }
        if (newEventDto.getAnnotation() != null)
            eventToUpdate.setAnnotation(newEventDto.getAnnotation());
        if (newEventDto.getCategory() != null)
            eventToUpdate.setCategory(adminService.findCategoryById(newEventDto.getCategory()));
        if (newEventDto.getDescription() != null)
            eventToUpdate.setDescription(newEventDto.getDescription());
        if (newEventDto.getLocation() != null) {
            locationRepository.save(newEventDto.getLocation());
            log.info("Добавлена локация с Id = {}", newEventDto.getLocation().getId());
            eventToUpdate.setLocation(newEventDto.getLocation());
        }
        if (newEventDto.getPaid() != null)
            eventToUpdate.setPaid(newEventDto.getPaid());
        if (newEventDto.getParticipantLimit() != null)
            eventToUpdate.setParticipantLimit(newEventDto.getParticipantLimit());
        if (newEventDto.getRequestModeration() != null)
            eventToUpdate.setRequestModeration(newEventDto.getRequestModeration());
        if(newEventDto.getStateAction().equals(EventActionState.CANCEL_REVIEW))
            eventToUpdate.setState(EventState.CANCELED);
        if (newEventDto.getTitle() != null)
            eventToUpdate.setTitle(newEventDto.getTitle());

        eventRepository.save(eventToUpdate);
        log.info("Обновлено событие с Id = {}", eventToUpdate.getId());
        return eventToUpdate;
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId)));
    }

}
