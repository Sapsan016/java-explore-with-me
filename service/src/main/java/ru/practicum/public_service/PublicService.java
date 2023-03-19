package ru.practicum.public_service;

import ru.practicum.model.Category;

import java.util.List;

public interface PublicService {
    List<Category> getCategories(Integer from, Integer size);
}
