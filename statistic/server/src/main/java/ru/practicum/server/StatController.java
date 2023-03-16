package ru.practicum.server;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitAddDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping(path = "/stats")                                                  //Получение статистики за период
    public List<HitDto> getStats(@RequestParam String start,
                                 @RequestParam String end,
                                 @RequestParam(required = false) String[] uris) {


        if(uris.length != 0) {
            List<HitDto> list = new ArrayList<>();
            for(String uri : uris) {
                list.add(statService.getUriStats(start, end, uri));
            }

            return list.stream()
                    .sorted(Comparator.comparingInt(HitDto::getHits).reversed())
                    .collect(Collectors.toList());
        }

        return statService.getStats(start, end).stream()
                .map(HitMapper::toHitDto)
                .collect(Collectors.toList());
    }

}
