package ru.practicum.server;

import ru.practicum.dto.HitAddDto;
import ru.practicum.dto.HitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;


public interface StatService {
    EndpointHit addHit(HitAddDto hitAddDto);

    HitDto getUriStats(LocalDateTime start, LocalDateTime end, String uri);

    HitDto getUniqueIpStats(LocalDateTime start, LocalDateTime end, String uri);
}
