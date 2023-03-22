package ru.practicum.public_service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.CompEventDAO;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.repositories.EventRepository;

import java.util.ArrayList;
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

    public PublicServiceImpl(CategoryRepository categoryRepository, AdminService adminService,
                             CompilationRepository compilationRepository, CompEventDAO compEventDAO,
                             EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.adminService = adminService;
        this.compilationRepository = compilationRepository;
        this.compEventDAO = compEventDAO;
        this.eventRepository = eventRepository;
    }


    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        log.info("Выполняется поиск всех категорий событий пропуская первые: {} , размер списка: {}", from, size);
        return categoryRepository.getAllCategories(from, size);
    }

    @Override
    public Category getCategoryById(Long catId) {
        log.info("Выполняется поиск всех категории событий Id = {}", catId);
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
        log.info("Выполняется поиск категории событий Id = {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation with id=%s was not found", compId)));
        List<EventShortDto> compEvents = compEventDAO.getAllEventsId(compId)
                .stream()
                .map(id -> eventRepository.findById(id).get())
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        compilation.setEvents(compEvents);
        return compilation;
    }

    public List<Compilation> addCompEvents(List<Compilation> compilations) {
        log.info("Выполняется поиск и добавленние событий к категориям из списка: {}", compilations);
        for(Compilation compilation : compilations) {
            List<Long> compEvIds = compEventDAO.getAllEventsId(compilation.getId());
            List<Event> compEvents = new ArrayList<>();
            for(Long id : compEvIds) {
                compEvents.add(eventRepository.findById(id).get());
            }
            compilation.setEvents(compEvents
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList()));
        }
        return compilations;
    }


}
