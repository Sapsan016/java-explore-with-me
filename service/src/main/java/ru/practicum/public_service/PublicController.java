package ru.practicum.public_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CatDto;
import ru.practicum.dto.category.CatMapper;
import ru.practicum.dto.users.UserDto;
import ru.practicum.dto.users.UserMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
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
}
