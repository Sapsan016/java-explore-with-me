package ru.practicum.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.AddCatDto;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.events.EventFullDto;
import ru.practicum.dto.events.requests.UpdateEventRequest;
import ru.practicum.mappers.CatMapper;
import ru.practicum.dto.users.AddUserDto;
import ru.practicum.dto.users.UserDto;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.UserMapper;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

   public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false, defaultValue = "") Long[] ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("AdminController: Получен запрос на поиск пользователей с номерами Id {}, " +
                "пропуская первых {}, размер списка = {}", Arrays.toString(ids), from, size);

        return adminService.getUsers(ids, from, size)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Long userId) {
        log.info("AdminController: Получен запрос на удаление пользователя ID = {}", userId);
        adminService.removeUser(userId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Valid UpdateEventRequest updateEventDto,
                                    @PathVariable Long eventId) {
        log.info("AdminController: Получен запрос на обновления события Id = {}", eventId);
        return EventMapper.toEventFullDto(adminService.updateEvent(updateEventDto, eventId));
    }

//To do
//    @GetMapping("/events")
//    public List<EventFullDto> getEvents(@RequestParam(required = false, defaultValue = "") Long[] users,
//                                        @RequestParam(required = false, defaultValue = "") String[] states,
//                                        @RequestParam(required = false, defaultValue = "") Long[] categories,
//                                        @RequestParam(required = false) String rangeStart,
//                                        @RequestParam(required = false) String rangeEnd,
//                                        @RequestParam(defaultValue = "0") Integer from,
//                                        @RequestParam(defaultValue = "10") Integer size) {
//        log.info("AdminController: Получен запрос на поиск событий, добавленных пользователями с номерами Id {}, " +
//                        "состояния событий: {}, категории событий: {}, события должны произойте не раньше чем: {}, " +
//                        " и не позже чем: {} пропуская первых {}, размер списка = {}",
//                Arrays.toString(users), Arrays.toString(states), Arrays.toString(categories), rangeStart, rangeEnd,
//                from, size);
//
////        return adminService.getEvents(users, states, categories, LocalDateTime.parse(rangeStart, FORMATTER),
////                        LocalDateTime.parse(rangeEnd, FORMATTER), from, size)
////                .stream()
////                .map(EventMapper::toEventFullDto)
////                .collect(Collectors.toList());
// //   }


}
