package ru.practicum.dto.users;

import ru.practicum.model.User;

public class UserMapper {
    public static User toUser(AddUserDto addUserDto) {
        return new User(
                null,
                addUserDto.getName(),
                addUserDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getEmail(),
                user.getId(),
                user.getName()
        );
    }
}
