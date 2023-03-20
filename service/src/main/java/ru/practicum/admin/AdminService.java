package ru.practicum.admin;

import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.model.Category;
import ru.practicum.model.User;

import java.util.List;

public interface AdminService {
    Category createCategory(AddCatDto category);

    void removeCategory(Long itemId);

    Category alterCategory(Long catId, AddCatDto category);

    User addUser(AddUserDto addUserDto);

    void removeUser(Long userId);

    List<User> getUsers(Long[] ids, Integer from, Integer size);

    Category findCategoryById(Long catId);

    User findUserById(Long userId);
}
