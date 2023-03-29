package ru.practicum.public_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.states.RequestState;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repositories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PublicServiceImpl implements PublicService {

    CategoryRepository categoryRepository;
    AdminService adminService;

    CompilationRepository compilationRepository;

    CompEventDAO compEventDAO;

    EventRepository eventRepository;

    RequestRepository requestRepository;

    public PublicServiceImpl(CategoryRepository categoryRepository, AdminService adminService,
                             CompilationRepository compilationRepository, CompEventDAO compEventDAO,
                             EventRepository eventRepository, RequestRepository requestRepository) {
        this.categoryRepository = categoryRepository;
        this.adminService = adminService;
        this.compilationRepository = compilationRepository;
        this.compEventDAO = compEventDAO;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }


    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        log.info("Выполняется поиск всех категорий событий пропуская первые: {} , размер списка: {}", from, size);
        return categoryRepository.getAllCategories(from, size);
    }

    @Override
    public Category getCategoryById(Long catId) {
        log.info("Выполняется поиск всех категории событий ID = {}", catId);
        return adminService.findCategoryById(catId);
    }

    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {
        if (pinned) {
            log.info("Выполняется поиск закрепленных категорий событий пропуская первые: {}, " +
                    "размер списка: {}", from, size);
            List<Compilation> pinnedCompilations = compilationRepository.getAllPinnedCompilations(from, size);
            return addCompEvents(pinnedCompilations);
        }
        log.info("Выполняется поиск всех категорий событий пропуская первые: {} , размер списка: {}", from, size);
        List<Compilation> allCompilations = compilationRepository.getAllCompilations(from, size);
        return addCompEvents(allCompilations);
    }

    @Override
    public Compilation getCompilationById(Long compId) {
        log.info("Выполняется поиск категории событий ID = {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Подборка событий с ID=%s не найдена", compId)));
        List<EventShortDto> compEvents = compEventDAO.getAllEventsId(compId)
                .stream()
                .map(id -> eventRepository.findById(id).get())
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        compilation.setEvents(compEvents);
        return compilation;
    }

    @Override
    public List<Event> searchEventsAfterStartRange(String text, Long[] categories, Boolean paid,
                                                   LocalDateTime startRange, Boolean onlyAvailable,
                                                   String sort, Integer from, Integer size) {
        List<Event> foundEvents = eventRepository.searchEventsAfterStartRange(text.toLowerCase(), startRange);
        addViews(foundEvents);
        return filterEvents(foundEvents, categories, paid, onlyAvailable, sort, from, size);
    }

    @Override
    public List<Event> searchEventsBeforeEndRange(String text, Long[] categories, Boolean paid, LocalDateTime endRange,
                                                  Boolean onlyAvailable, String sort, Integer from, Integer size) {
        List<Event> foundEvents = eventRepository.searchEventsBeforeEndRange(text.toLowerCase(), endRange);
        addViews(foundEvents);
        return filterEvents(foundEvents, categories, paid, onlyAvailable, sort, from, size);
    }

    @Override
    public List<Event> searchEventsWithStartAndEndRange(String text, Long[] categories, Boolean paid,
                                                        LocalDateTime startRange, LocalDateTime endRange,
                                                        Boolean onlyAvailable, String sort, Integer from,
                                                        Integer size) {
        List<Event> foundEvents = eventRepository.searchWithStartEnd(text.toLowerCase(), startRange, endRange);
        addViews(foundEvents);
        return filterEvents(foundEvents, categories, paid, onlyAvailable, sort, from, size);
    }

    @Override
    public Event getEventById(Long eventId) {
        log.info("Выполняется поиск события Id = {}", eventId);
         Event foundEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Подборка событий ID=%s не найдена", eventId)));
         foundEvent.setViews(foundEvent.getViews() + 1);
        return foundEvent;
    }

    private List<Event> filterEvents(List<Event> foundEvents, Long[] categories, Boolean paid, Boolean onlyAvailable,
                                     String sort, Integer from, Integer size) {
        if (categories.length != 0) {
            for (Long category : categories) {
                foundEvents = foundEvents.stream()
                        .filter(event -> event.getCategory().equals(adminService.findCategoryById(category)))
                        .collect(Collectors.toList());
            }
        }
        if (onlyAvailable) {
            foundEvents = foundEvents.stream()
                    .filter(event -> event.getParticipantLimit() > requestRepository
                            .countParticipationRequestsByEventAndStatus(event.getId(), RequestState.CONFIRMED))
                    .collect(Collectors.toList());
        }
        if (sort.equals("EVENT_DATE")) {
            return foundEvents.stream()
                    .filter(event -> event.getPaid().equals(paid))
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        if (sort.equals("RATE")) {
            return foundEvents.stream()
                    .filter(event -> event.getPaid().equals(paid))
                    .sorted(Comparator.comparing(Event::getRate))
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        return foundEvents.stream()
                .filter(event -> event.getPaid().equals(paid))
                .sorted(Comparator.comparing(Event::getViews))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }


    public List<Compilation> addCompEvents(List<Compilation> compilations) {
        log.info("Выполняется поиск и добавленние событий к категориям из списка: {}", compilations);
        for (Compilation compilation : compilations) {
            List<Long> compEvIds = compEventDAO.getAllEventsId(compilation.getId());
            List<Event> compEvents = new ArrayList<>();
            for (Long id : compEvIds) {
                compEvents.add(eventRepository.findById(id).get());
            }
            compilation.setEvents(compEvents
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList()));
        }
        return compilations;
    }

    private void addViews(List<Event> foundEvents) {
        foundEvents.forEach(event -> event.setViews(event.getViews() + 1));
        eventRepository.saveAll(foundEvents);
    }

}
