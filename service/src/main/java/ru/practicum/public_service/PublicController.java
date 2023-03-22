package ru.practicum.public_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.mappers.CatMapper;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.EventMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@Slf4j
public class PublicController {
    private final PublicService publicService;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/categories")
    public List<CatDto> getStats(@RequestParam(defaultValue = "0") Integer from,
                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicController: Получен запрос на поиск категорий событий пропуская первые {}, размер списка = {}",
                from, size);

        return publicService.getCategories(from, size)
                .stream()
                .map(CatMapper::toCatDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/categories/{catId}")
    public CatDto getCategoryById(@PathVariable Long catId) {
        log.info("PublicController: Получен запрос на поиск категории с Id = {}", catId);
        return CatMapper.toCatDto(publicService.getCategoryById(catId));
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicController: Получен запрос на поиск подборок событий, закрепленных {}, " +
                "пропуская первые {}, размер списка = {}", pinned, from, size);

        return publicService.getCompilations(pinned, from, size)
                .stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("PublicController: Получен запрос на поиск подборки событий с Id = {}", compId);
        return CompilationMapper.toDto(publicService.getCompilationById(compId));
    }

    @GetMapping("/events")
    public List<EventShortDto> searchEvents(@RequestParam("text") String text,
                                            @RequestParam(defaultValue = "") Long[] categories,
                                            @RequestParam(defaultValue = "false") Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(defaultValue = "") String sort,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicController: Получен запрос на поиск событий, имеющих в аннотации и подробном описании события" +
                        "текст {}, категории событий: {}, платные события: {}, события должны произойте не раньше " +
                        "чем: {}, и не позже чем: {} , только доступные события: {}, сортировка по {}, " +
                        "пропуская первых {}, размер списка = {}",
                text, Arrays.toString(categories), paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        if (rangeStart == null && rangeEnd == null) {
            return publicService.searchEventsAfterStartRange(text, categories, paid, LocalDateTime.now(), onlyAvailable,
                            sort, from, size).stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }

        if (rangeEnd == null) {
            return publicService.searchEventsAfterStartRange(text, categories, paid, LocalDateTime.parse(rangeStart,
                            FORMATTER), onlyAvailable, sort, from, size).stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());

        }
        if (rangeStart == null) {
            return publicService.searchEventsBeforeEndRange(text, categories, paid, LocalDateTime.parse(rangeEnd,
                            FORMATTER), onlyAvailable, sort, from, size).stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());

        }
        return publicService.searchEventsWithStartAndEndRange(text, categories, paid, LocalDateTime.parse(rangeStart,
                                FORMATTER), LocalDateTime.parse(rangeEnd, FORMATTER), onlyAvailable, sort, from, size)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());


    }
}

