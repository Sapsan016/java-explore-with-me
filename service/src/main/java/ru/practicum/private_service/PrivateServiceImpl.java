package ru.practicum.private_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;
import ru.practicum.repositories.UserRepository;

import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PrivateServiceImpl implements PrivateService {

    EventRepository eventRepository;
    LocationRepository locationRepository;

    UserRepository userRepository;
    CategoryRepository categoryRepository;

    AdminService adminService;

    public PrivateServiceImpl(EventRepository eventRepository, LocationRepository locationRepository,
                              UserRepository userRepository, CategoryRepository categoryRepository,
                              AdminService adminService) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.adminService = adminService;
    }

    @Override
    public Event addEvent(NewEventDto newEventDto, Long userId) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с id %s не найден", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Категория с id %s не найдена", newEventDto.getCategory())));
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
}
