package ru.practicum.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.category.CatMapper;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.dto.users.UserDto;
import ru.practicum.dto.users.UserMapper;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CatDto addCategory(@RequestBody @Valid AddCatDto category) {
        log.info("AdminController: Получен запрос на создание категории {}", category.getName());
        return CatMapper.toCatDto(adminService.createCategory(category));
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long catId) {
        log.info("AdminController: Получен запрос на удаление категории ID = {}", catId);
        adminService.removeCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CatDto alterCategory(@PathVariable Long catId, @RequestBody @Valid AddCatDto category) {
        log.info("AdminController: Получен запрос на изменение категории ID = {}", catId);
        return CatMapper.toCatDto(adminService.alterCategory(catId, category));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid AddUserDto addUserDto) {
        log.info("AdminController: Получен запрос на добавление пользователя {}", addUserDto.toString());
        return UserMapper.toUserDto(adminService.addUser(addUserDto));
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Long userId) {
        log.info("AdminController: Получен запрос на удаление пользователя ID = {}", userId);
        adminService.removeUser(userId);
    }

}
