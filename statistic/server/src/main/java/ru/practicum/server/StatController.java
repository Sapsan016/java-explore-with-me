package ru.practicum.server;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public HitDto addEndpointHit(@RequestBody HitAddDto hitAddDto) {
        System.out.println(hitAddDto);
        return HitMapper.toHitDto(statService.addHit(hitAddDto));
    }

}
