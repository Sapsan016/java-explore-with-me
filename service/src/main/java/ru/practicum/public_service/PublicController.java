package ru.practicum.public_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.mappers.CatMapper;
import ru.practicum.mappers.CompilationMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@Slf4j
public class PublicController {
    private final PublicService publicService;

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
}
