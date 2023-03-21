package ru.practicum.admin;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.dto.events.states.EventActionStates;
import ru.practicum.mappers.CatMapper;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.mappers.UserMapper;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.LocationRepository;
import ru.practicum.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    public AdminServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository,
                            EventRepository eventRepository, LocationRepository locationRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public Category createCategory(AddCatDto addCatDto) {
        Category category = CatMapper.toCat(addCatDto);
        categoryRepository.save(category);
        log.info("Добавлена категория с Id = {}", category.getId());
        return category;
    }

    @Override
    public void removeCategory(Long catId) {
        Category catToRemove = findCategoryById(catId);
        categoryRepository.delete(catToRemove);
        log.info("Удалена категория с Id = {}", catId);
    }

    @Override
    public Category alterCategory(Long catId, AddCatDto category) {
        Category catToAlter = findCategoryById(catId);
        catToAlter.setName(category.getName());
        categoryRepository.save(catToAlter);
        log.info("Изменена категория с Id = {}", catToAlter.getId());
        return catToAlter;
    }

    @Override
    public User addUser(AddUserDto addUserDto) {
        User user = UserMapper.toUser(addUserDto);
        userRepository.save(user);
        log.info("Добавлен пользователь с Id = {}", user.getId());
        return user;
    }


    @Override
    public void removeUser(Long userId) {
        User userToRemove = findUserById(userId);
        userRepository.delete(userToRemove);
        log.info("Удален пользователь с Id = {}", userId);
    }

    @Override
    public List<User> getUsers(Long[] ids, Integer from, Integer size) {
        if (ids.length == 0) {
            log.info("Выполняется поиск всех пользователей пропуская первых {}, размер списка {}", from, size);
            return userRepository.getAllUsers(from, size);
        }
        log.info("Выполняется поиск всех пользователей с id {} пропуская первых {}, размер списка {}",
                Arrays.toString(ids), from, size);
        try {
            return Arrays.stream(ids).map(this::findUserById)
                    .skip(from)
                    .limit(size)
                    .collect(Collectors.toList());
        } catch (ObjectNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Category findCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Category with id=%s was not found", catId)));
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with id=%s was not found", userId)));
    }


    //To do
    @Override
    public List<Event> getEvents(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Integer from, Integer size) {
//        List<User> userList = getUsers(users, 0, Integer.MAX_VALUE);
//        EventState[] eventStates = (EventState[]) Arrays.stream(states).map(EventState::valueOf).toArray();

        log.info("Выполняется поиск всех событий, добавленных пользователями с номерами Id {}," +
                        "состояния событий: {}, категории событий: {}, события должны произойте не раньше чем: {}, " +
                        "и не позже чем: {} пропуская первых {}, размер списка = {}.",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventRepository.findEventsByUserIdAndStateAndEventDateBetween(
                users, states, categories, rangeStart, rangeEnd, from, size);

    }

    @Override
    public Event updateEvent(UpdateEventRequest updateEventDto, Long eventId) {
        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId)));


        if (eventToUpdate.getEventDate().isBefore(LocalDateTime.now().plusHours(1)))
            throw new IllegalArgumentException("The event starts in less than 1 hour from now " +
                    "and could not be changed");
        eventToUpdate = checkUpdateEvent(eventToUpdate, updateEventDto);

        if (updateEventDto.getStateAction().equals(EventActionStates.PUBLISH_EVENT))
            eventToUpdate.setState(EventState.PUBLISHED);
        if (updateEventDto.getStateAction().equals(EventActionStates.REJECT_EVENT))
            eventToUpdate.setState(EventState.CANCELED);
        eventRepository.save(eventToUpdate);
        log.info("Обновлено событие с Id = {}", eventToUpdate.getId());
        return eventToUpdate;
    }

    @Override
    public Event checkUpdateEvent(Event eventToUpdate, UpdateEventRequest newEventDto) {
        if (eventToUpdate.getState().equals(EventState.PUBLISHED))
            throw new IllegalArgumentException("Only pending or canceled events can be changed");
        if (newEventDto.getAnnotation() != null)
            eventToUpdate.setAnnotation(newEventDto.getAnnotation());
        if (newEventDto.getCategory() != null)
            eventToUpdate.setCategory(findCategoryById(newEventDto.getCategory()));
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
        if (newEventDto.getTitle() != null)
            eventToUpdate.setTitle(newEventDto.getTitle());
        return eventToUpdate;
    }

}
