package ru.practicum.server;

import ru.practicum.dto.hit.HitAddDto;
import ru.practicum.dto.hit.HitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;


public interface StatService {
    EndpointHit addHit(HitAddDto hitAddDto);

    HitDto getUriStats(LocalDateTime start, LocalDateTime end, String uri);

    HitDto getUniqueIpStats(LocalDateTime start, LocalDateTime end, String uri);

    List<HitDto> getAllStats(LocalDateTime start, LocalDateTime end);
}
