package ru.practicum.admin;

import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    Category createCategory(AddCatDto category);

    void removeCategory(Long itemId);

    Category alterCategory(Long catId, AddCatDto category);

    User addUser(AddUserDto addUserDto);

    void removeUser(Long userId);

    List<User> getUsers(Long[] ids, Integer from, Integer size, String sort);

    Category findCategoryById(Long catId);

    User findUserById(Long userId);

    List<Event> getEventsWithTime(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Integer from, Integer size);

    Event updateEvent(UpdateEventRequest updateEventDto, Long eventId);

    Event checkUpdateEvent(Event eventToUpdate, UpdateEventRequest newEventDto);

    Compilation addCompilation(NewCompilationDto newCompilationDto);

    void removeCompilation(Long compId);

    Compilation alterCompilation(Long compId, NewCompilationDto newCompDto);


    List<Event> getEventsWithoutTime(Long[] users, String[] states, Long[] categories, Integer from, Integer size);

    List<Event> getEventsWithStartTimeParamTime(Long[] users, String[] states, Long[] categories,
                                                LocalDateTime rangeStart, Integer from, Integer size);

    List<Event> getEventsWithEndTimeParamTime(Long[] users, String[] states, Long[] categories, LocalDateTime rangeEnd,
                                              Integer from, Integer size);
}
