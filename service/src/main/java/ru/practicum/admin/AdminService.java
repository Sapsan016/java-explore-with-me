package ru.practicum.admin;

import ru.practicum.dto.category.AddCatDto;
import ru.practicum.model.Category;

public interface AdminService {
    Category createCategory(AddCatDto category);

    void removeCategory(Long itemId);
}
