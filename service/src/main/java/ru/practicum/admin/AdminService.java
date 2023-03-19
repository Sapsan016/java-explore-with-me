package ru.practicum.admin;

import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.model.Category;
import ru.practicum.model.User;

public interface AdminService {
    Category createCategory(AddCatDto category);

    void removeCategory(Long itemId);

    Category alterCategory(Long catId, AddCatDto category);

    User addUser(AddUserDto addUserDto);

    void removeUser(Long userId);
}
