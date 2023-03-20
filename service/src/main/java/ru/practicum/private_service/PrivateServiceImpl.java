package ru.practicum.private_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PrivateServiceImpl implements PrivateService {

    EventRepository eventRepository;
    LocationRepository locationRepository;

    public PrivateServiceImpl(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public Event addEvent(NewEventDto newEventDto, Long userId) {
        Event eventToAdd = EventMapper.toEvent(newEventDto, userId);
        locationRepository.save(eventToAdd.getLocation());
        log.info("Добавлена локация с Id = {}", eventToAdd.getLocation().getId());
        eventRepository.save(eventToAdd);
        log.info("Добавлено событие с Id = {}", eventToAdd.getId());
        return eventToAdd;
    }
}
