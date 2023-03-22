package ru.practicum.public_service;

import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PublicService {
    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Long catId);

    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilationById(Long compId);


    List<Event> searchEventsAfterStartRange(String text, Long[] categories, Boolean paid,
                                            LocalDateTime startRange, Boolean onlyAvailable, String sort, Integer from,
                                            Integer size);

    List<Event> searchEventsBeforeEndRange(String text, Long[] categories, Boolean paid, LocalDateTime endRange,
                                           Boolean onlyAvailable, String sort, Integer from, Integer size);

    List<Event> searchEventsWithStartAndEndRange(String text, Long[] categories, Boolean paid, LocalDateTime startRange,
                                                 LocalDateTime endRange, Boolean onlyAvailable, String sort,
                                                 Integer from, Integer size);
}
