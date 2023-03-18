package ru.practicum.server;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitAddDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitMapper;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StatController {

    private final StatService statService;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatController(StatService statService) {
        this.statService = statService;
    }


    @PostMapping(path = "/hit")                                        //Сохранение информации о запросе на эндпойнт
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto addEndpointHit(@RequestBody @Valid HitAddDto hitAddDto) {
        return HitMapper.toHitDto(statService.addHit(hitAddDto));
    }

    @GetMapping(path = "/stats")                                                  //Получение статистики за период
    public List<HitDto> getStats(@RequestParam String start,
                                 @RequestParam String end,
                                 @RequestParam(required = false) String[] uris,
                                 @RequestParam(defaultValue = "false") boolean unique) {
        List<HitDto> list = new ArrayList<>();
        if (unique) {                                                        //Получение статистики для уникального ip
            for (String uri : uris) {
                list.add(statService.getUniqueIpStats(LocalDateTime.parse(start, FORMATTER),
                        LocalDateTime.parse(end, FORMATTER), uri));
            }
        } else {
            for (String uri : uris) {
                list.add(statService.getUriStats(LocalDateTime.parse(start, FORMATTER),
                        LocalDateTime.parse(end, FORMATTER), uri));
            }
        }
        return list.stream()
                .sorted(Comparator.comparingInt(HitDto::getHits).reversed())
                .collect(Collectors.toList());
    }
}
