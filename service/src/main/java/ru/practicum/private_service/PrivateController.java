package ru.practicum.private_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.events.EventFullDto;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.mappers.EventMapper;

import javax.validation.Valid;

@RestController
@RequestMapping
@Slf4j
public class PrivateController {

    private final PrivateService privateService;


    public PrivateController(PrivateService privateService) {
        this.privateService = privateService;
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@RequestBody @Valid NewEventDto newEventDto, @PathVariable Long userId) {
        log.info("PublicController: Получен запрос на добавление события {} от пользователя Id = {}",
                newEventDto.toString(), userId);
        return EventMapper.toEventFullDto(privateService.addEvent(newEventDto, userId));
    }

}
