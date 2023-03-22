package ru.practicum.public_service;

import ru.practicum.model.Category;
import ru.practicum.model.Compilation;

import java.util.Collection;
import java.util.List;

public interface PublicService {
    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Long catId);

    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);
}
