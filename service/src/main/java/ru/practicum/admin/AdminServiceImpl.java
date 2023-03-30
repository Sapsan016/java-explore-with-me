package ru.practicum.admin;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.dto.events.states.EventActionStates;
import ru.practicum.dto.events.states.EventState;
import ru.practicum.mappers.CatMapper;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.*;
import ru.practicum.repositories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminServiceImpl implements AdminService {

    CategoryRepository categoryRepository;
    UserRepository userRepository;

    EventRepository eventRepository;

    LocationRepository locationRepository;

    CompilationRepository compilationRepository;

    CompEventDAO compEventDAO;

    static String UP = "ASC";
    static String DOWN = "DESC";


    public AdminServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository,
                            EventRepository eventRepository, LocationRepository locationRepository,
                            CompilationRepository compilationRepository, CompEventDAO compEventDAO) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.compilationRepository = compilationRepository;
        this.compEventDAO = compEventDAO;
    }

    @Override
    public Category createCategory(AddCatDto addCatDto) {
        Category category = CatMapper.toCat(addCatDto);
        categoryRepository.save(category);
        log.info("Добавлена категория с ID = {}", category.getId());
        return category;
    }

    @Override
    public void removeCategory(Long catId) {
        if (!eventRepository.getEventsByCategoryId(catId, 0, 1).isEmpty())
            throw new DataIntegrityViolationException("The category is not empty");
        Category catToRemove = findCategoryById(catId);
        categoryRepository.delete(catToRemove);
        log.info("Удалена категория с ID = {}", catId);
    }

    @Override
    public Category alterCategory(Long catId, AddCatDto category) {
        Category catToAlter = findCategoryById(catId);
        catToAlter.setName(category.getName());
        categoryRepository.save(catToAlter);
        log.info("Изменена категория с ID = {}", catToAlter.getId());
        return catToAlter;
    }

    @Override
    public User addUser(AddUserDto addUserDto) {
        User user = UserMapper.toUser(addUserDto);
        userRepository.save(user);
        log.info("Добавлен пользователь с ID = {}", user.getId());
        return user;
    }


    @Override
    public void removeUser(Long userId) {
        User userToRemove = findUserById(userId);
        userRepository.delete(userToRemove);
        log.info("Удален пользователь с ID = {}", userId);
    }

    @Override
    public List<User> getUsers(Long[] ids, Integer from, Integer size, String sort) {
        if (ids.length == 0) {
            log.info("Выполняется поиск всех пользователей пропуская первых {}, размер списка {}, " +
                    "сортировка по рейтингу {}", from, size, sort);
            List<User> foundUsers = userRepository.getAllUsers(from, size);
            return sortAndReturnUsers(foundUsers, from, size, sort);
        }
        log.info("Выполняется поиск всех пользователей с ID {} пропуская первых {}, размер списка {}",
                Arrays.toString(ids), from, size);
        try {
            List<User> foundUsers = Arrays.stream(ids).map(this::findUserById).collect(Collectors.toList());
            return sortAndReturnUsers(foundUsers, from, size, sort);
        } catch (ObjectNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void findEventsByUser(List<Event> foundEvents, Long[] users) {
        for (Long user : users) {
            foundEvents.addAll(eventRepository.getAllEventsByUserIdAndLimit(user, 0, Integer.MAX_VALUE));
        }
    }

    private void findEventsByState(List<Event> foundEvents, String[] states) {
        for (String state : states) {
            foundEvents.addAll(eventRepository.findEventByState(state));
        }
    }

    private void findEventsByCategoryAndFromSize(List<Event> foundEvents, Long[] categories, Integer from,
                                                 Integer size) {
        for (Long category : categories) {
            foundEvents.addAll(eventRepository.getEventsByCategoryId(category, from, size));
        }
    }

    @Override
    public List<Event> getEventsWithTime(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Integer from, Integer size, String sort) {
        List<Event> foundEvents = new ArrayList<>();
        if (users.length != 0) {
            foundEvents = findEvents(users, states, categories, foundEvents);
            return foundEvents.stream()
                    .filter(event -> event.getEventDate().isAfter(rangeStart))
                    .filter(event -> event.getEventDate().isBefore(rangeEnd))
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        if (states.length != 0) {
            findEventsByState(foundEvents, states);
            if (categories.length != 0) {
                for (Long category : categories) {
                    foundEvents = foundEvents.stream()
                            .filter(event -> event.getCategory().getId().equals(category))
                            .collect(Collectors.toList());
                }
            }
            return foundEvents.stream()
                    .filter(event -> event.getEventDate().isAfter(rangeStart))
                    .filter(event -> event.getEventDate().isBefore(rangeEnd))
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        if (categories.length != 0) {
            findEventsByCategoryAndFromSize(foundEvents, categories, from, size);
            return foundEvents;
        }
        return eventRepository.getEventsByTimeAndFromAndSize(rangeStart, rangeEnd, from, size);
    }

    @Override
    public List<Event> getEventsWithStartTimeParamTime(Long[] users, String[] states, Long[] categories,
                                                       LocalDateTime rangeStart, Integer from, Integer size,
                                                       String sort) {
        List<Event> foundEvents = new ArrayList<>();
        if (users.length != 0) {
            foundEvents = findEvents(users, states, categories, foundEvents);
            foundEvents = foundEvents.stream()
                    .filter(event -> event.getEventDate().isAfter(rangeStart))
                    .collect(Collectors.toList());
            return sortLimitAndReturnEvents(foundEvents, from, size, sort);
        }
        if (states.length != 0) {
            findEventsByState(foundEvents, states);
            if (categories.length != 0) {
                for (Long category : categories) {
                    foundEvents = foundEvents.stream()
                            .filter(event -> event.getCategory().getId().equals(category))
                            .collect(Collectors.toList());
                }
            }
            foundEvents = foundEvents.stream()
                    .filter(event -> event.getEventDate().isAfter(rangeStart))
                    .collect(Collectors.toList());
            return sortLimitAndReturnEvents(foundEvents, from, size, sort);
        }
        if (categories.length != 0) {
            findEventsByCategoryAndFromSize(foundEvents, categories, from, size);
            return sortAndReturnEvents(foundEvents, sort);
        }
        foundEvents = eventRepository.getEventsByStartTimeAndFromAndSize(rangeStart, from, size);
        return sortAndReturnEvents(foundEvents, sort);
    }

    private List<Event> findEvents(Long[] users, String[] states, Long[] categories, List<Event> foundEvents) {
        findEventsByUser(foundEvents, users);
        if (states.length != 0) {
            for (String state : states) {
                foundEvents = foundEvents.stream()
                        .filter(event -> event.getState().toString().equals(state))
                        .collect(Collectors.toList());
            }
        }
        if (categories.length != 0) {
            for (Long category : categories) {
                foundEvents = foundEvents.stream()
                        .filter(event -> event.getCategory().getId().equals(category))
                        .collect(Collectors.toList());
            }
        }
        return foundEvents;
    }


    @Override
    public List<Event> getEventsWithEndTimeParamTime(Long[] users, String[] states, Long[] categories,
                                                     LocalDateTime rangeEnd, Integer from, Integer size, String sort) {
        List<Event> foundEvents = new ArrayList<>();
        if (users.length != 0) {
            foundEvents = findEvents(users, states, categories, foundEvents);
            foundEvents = foundEvents.stream()
                    .filter(event -> event.getEventDate().isBefore(rangeEnd))
                    .collect(Collectors.toList());
            return sortLimitAndReturnEvents(foundEvents, from, size, sort);
        }
        if (states.length != 0) {
            findEventsByState(foundEvents, states);
            if (categories.length != 0) {
                for (Long category : categories) {
                    foundEvents = foundEvents.stream()
                            .filter(event -> event.getCategory().getId().equals(category))
                            .collect(Collectors.toList());
                }
            }
            foundEvents = foundEvents.stream()
                    .filter(event -> event.getEventDate().isBefore(rangeEnd))
                    .collect(Collectors.toList());
            return sortLimitAndReturnEvents(foundEvents, from, size, sort);
        }
        if (categories.length != 0) {
            findEventsByCategoryAndFromSize(foundEvents, categories, from, size);
            return sortAndReturnEvents(foundEvents, sort);
        }
        foundEvents = eventRepository.getEventsByEndTimeAndFromAndSize(rangeEnd, from, size);
        return sortAndReturnEvents(foundEvents, sort);
    }


    @Override
    public List<Event> getEventsWithoutTime(Long[] users, String[] states, Long[] categories,
                                            Integer from, Integer size, String sort) {
        List<Event> foundEvents = new ArrayList<>();
        if (users.length != 0) {
            foundEvents = findEvents(users, states, categories, foundEvents);
            return sortLimitAndReturnEvents(foundEvents, from, size, sort);
        }
        if (states.length != 0) {
            findEventsByState(foundEvents, states);
            if (categories.length != 0) {
                for (Long category : categories) {
                    foundEvents = foundEvents.stream()
                            .filter(event -> event.getCategory().getId().equals(category))
                            .collect(Collectors.toList());
                }
            }
            return sortLimitAndReturnEvents(foundEvents, from, size, sort);
        }
        if (categories.length != 0) {
            findEventsByCategoryAndFromSize(foundEvents, categories, from, size);
            return sortAndReturnEvents(foundEvents, sort);
        }
        foundEvents = eventRepository.getEventsByFromAndSize(from, size);
        return sortAndReturnEvents(foundEvents, sort);
    }

    @Override
    public Category findCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Категория с ID=%s не найдена", catId)));
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Пользователь с ID=%s не найден", userId)));
    }

    public Compilation findCompilationById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Подборка событий с ID=%s не найдена", compId)));
    }


    @Override
    public Event updateEvent(UpdateEventRequest updateEventDto, Long eventId) {
        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Событие с ID=%s не найдено", eventId)));
        if (eventToUpdate.getState().equals(EventState.CANCELED))
            throw new IllegalArgumentException("The event was cancelled");
        if (eventToUpdate.getEventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new IllegalArgumentException("Событие начинается менне чем через час " +
                    "и не может быть изменено");
        eventToUpdate = checkUpdateEvent(eventToUpdate, updateEventDto);
        if (updateEventDto.getStateAction().equals(EventActionStates.PUBLISH_EVENT)) {
            eventToUpdate.setState(EventState.PUBLISHED);
            eventToUpdate.setPublishedOn(LocalDateTime.now());
        }
        if (updateEventDto.getStateAction().equals(EventActionStates.REJECT_EVENT))
            eventToUpdate.setState(EventState.CANCELED);
        eventRepository.save(eventToUpdate);
        log.info("Обновлено событие: {}", eventToUpdate);
        return eventToUpdate;
    }

    @Override
    public Event checkUpdateEvent(Event eventToUpdate, UpdateEventRequest newEventDto) {
        if (eventToUpdate.getState().equals(EventState.PUBLISHED))
            throw new IllegalArgumentException("Событие уже было опубликовано");

        if (newEventDto.getAnnotation() != null)
            eventToUpdate.setAnnotation(newEventDto.getAnnotation());
        if (newEventDto.getCategory() != null)
            eventToUpdate.setCategory(findCategoryById(newEventDto.getCategory()));
        if (newEventDto.getDescription() != null)
            eventToUpdate.setDescription(newEventDto.getDescription());
        if (newEventDto.getLocation() != null) {
            locationRepository.save(newEventDto.getLocation());
            log.info("Добавлена локация с ID = {}", newEventDto.getLocation().getId());
            eventToUpdate.setLocation(newEventDto.getLocation());
        }
        if (newEventDto.getPaid() != null)
            eventToUpdate.setPaid(newEventDto.getPaid());
        if (newEventDto.getParticipantLimit() != null)
            eventToUpdate.setParticipantLimit(newEventDto.getParticipantLimit());
        if (newEventDto.getRequestModeration() != null)
            eventToUpdate.setRequestModeration(newEventDto.getRequestModeration());
        if (newEventDto.getTitle() != null)
            eventToUpdate.setTitle(newEventDto.getTitle());
        return eventToUpdate;
    }

    @Override
    public Compilation addCompilation(NewCompilationDto newCompilationDto) {
        List<Long> events = newCompilationDto.getEvents();
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilationRepository.save(compilation);
        log.info("Добавлена подборка событий с ID = {}", compilation.getId());
        if (events.size() > 0) {
            return addEventsToCompilation(compilation, events);
        }
        compilation.setEvents(new ArrayList<>());
        return compilation;
    }

    @Override
    public void removeCompilation(Long compId) {
        Compilation compToRemove = findCompilationById(compId);
        compilationRepository.delete(compToRemove);
        log.info("Удалена категория с ID = {}", compId);
    }

    @Override
    public Compilation alterCompilation(Long compId, NewCompilationDto newCompDto) {
        Compilation compToAlter = findCompilationById(compId);

        List<Long> events = newCompDto.getEvents();
        if (newCompDto.getPinned() != null)
            compToAlter.setPinned(newCompDto.getPinned());
        if (newCompDto.getTitle() != null)
            compToAlter.setTitle(newCompDto.getTitle());
        compilationRepository.save(compToAlter);
        log.info("Обновлена подборка событий с ID = {}", compToAlter.getId());
        if (events.size() > 0) {
            compEventDAO.removeCompEvents(compId);
            return addEventsToCompilation(compToAlter, events);
        }
        List<Long> savedEvents = compEventDAO.getAllEventsId(compId);
        compToAlter.setEvents(savedEvents.stream()
                .map(e -> eventRepository.findById(e).get())
                .map(EventMapper::toEventShortDto).collect(Collectors.toList()));
        return compToAlter;
    }


    public Compilation addEventsToCompilation(Compilation compilation, List<Long> events) {
        events.forEach(e -> compEventDAO.addNewCompEventPair(
                compilation.getId(), e));
        log.info("Добавлены события с Id = {} в подборку с ID = {}", events, compilation.getId());
        List<EventShortDto> compilationEvents = events.stream()
                .map(e -> eventRepository.findById(e).get())
                .map(EventMapper::toEventShortDto).collect(Collectors.toList());
        compilation.setEvents(compilationEvents);
        return compilation;
    }

    private List<User> sortAndReturnUsers(List<User> foundUsers, Integer from, Integer size, String sort) {
        if (sort.equals(UP)) {
            return foundUsers.stream()
                    .sorted(Comparator.comparing(User::getUserRate))
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        if (sort.equals(DOWN)) {
            return foundUsers.stream()
                    .sorted(Comparator.comparing(User::getUserRate).reversed())
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        return foundUsers.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    private List<Event> sortLimitAndReturnEvents(List<Event> foundEvents, Integer from, Integer size, String sort) {
        if (sort.equals(UP)) {
            return foundEvents.stream()
                    .sorted(Comparator.comparing(Event::getRate))
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        if (sort.equals(DOWN)) {
            return foundEvents.stream()
                    .sorted(Comparator.comparing(Event::getRate).reversed())
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        }
        return foundEvents.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    private List<Event> sortAndReturnEvents(List<Event> foundEvents, String sort) {
        if (sort.equals(UP)) {
            return foundEvents.stream()
                    .sorted(Comparator.comparing(Event::getRate))
                    .collect(Collectors.toList());
        }
        if (sort.equals(DOWN)) {
            return foundEvents.stream()
                    .sorted(Comparator.comparing(Event::getRate).reversed())
                    .collect(Collectors.toList());
        }
        return foundEvents;
    }

}
