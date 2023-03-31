package ru.practicum.server;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.hit.HitAddDto;
import ru.practicum.dto.hit.HitDto;
import ru.practicum.dto.hit.HitMapper;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j

public class StatController {

    private final StatService statService;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatController(StatService statService) {
        this.statService = statService;
    }


    @PostMapping(path = "/hit")                                        //Сохранение информации о запросе на эндпойнт
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto addEndpointHit(@RequestBody @Valid HitAddDto hitAddDto) {
        log.info("Получена информация: {} о запросе к эндпойнту", hitAddDto);
        return HitMapper.toHitDto(statService.addHit(hitAddDto));
    }

    @GetMapping(path = "/stats")                                                  //Получение статистики за период
    public List<HitDto> getStats(@RequestParam String start,
                                 @RequestParam String end,
                                 @RequestParam(required = false, defaultValue = "") String[] uris,
                                 @RequestParam(defaultValue = "false") boolean unique) {
        log.info("StatController: получен запрос на поиск статистики за период с: {} по: {}, для uri: {}, " +
                "выбирая только уникальные uri: {}", start, end, Arrays.toString(uris), unique);
        List<HitDto> list = new ArrayList<>();
        if (unique) {                                                        //Получение статистики для уникального uri
            for (String uri : uris) {
                list.add(statService.getUniqueIpStats(LocalDateTime.parse(start, FORMATTER),
                        LocalDateTime.parse(end, FORMATTER), uri));
            }
            return list
                    .stream()
                    .sorted(Comparator.comparingInt(HitDto::getHits).reversed())
                    .collect(Collectors.toList());

        } else if (uris.length > 0) {
            for (String uri : uris) {
                list.add(statService.getUriStats(LocalDateTime.parse(start, FORMATTER),
                        LocalDateTime.parse(end, FORMATTER), uri));
            }
            return list
                    .stream()
                    .sorted(Comparator.comparingInt(HitDto::getHits).reversed())
                    .collect(Collectors.toList());
        }
        list.addAll(statService.getAllStats(LocalDateTime.parse(start, FORMATTER),
                LocalDateTime.parse(end, FORMATTER)));
        return list
                .stream()
                .sorted(Comparator.comparingInt(HitDto::getHits).reversed())
                .collect(Collectors.toList());
    }
}
