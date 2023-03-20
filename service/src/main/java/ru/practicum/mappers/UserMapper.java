package ru.practicum.mappers;

import ru.practicum.dto.users.AddUserDto;
import ru.practicum.dto.users.UserDto;
import ru.practicum.dto.users.UserShortDto;
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

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
