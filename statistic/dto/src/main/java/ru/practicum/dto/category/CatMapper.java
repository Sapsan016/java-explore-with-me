package ru.practicum.dto.category;

import ru.practicum.model.Category;

public class CatMapper {
    public static Category toCat(AddCatDto addCatDto) {
        return new Category(
                null,
                addCatDto.getName()
        );
    }

    public static CatDto toCatDto(Category category) {
        return new CatDto(
                category.getId(),
                category.getName()
        );
    }
}
