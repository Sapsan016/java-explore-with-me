package ru.practicum.server;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.HitAddDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitMapper;

@RestController
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }


    @PostMapping(path = "/hit")                                        //Сохранение информации о запросе на эндпойнт
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto addEndpointHit(@RequestBody HitAddDto hitAddDto) {
        return HitMapper.toHitDto(statService.addHit(hitAddDto));
    }

}
