package ru.practicum.server;

import ru.practicum.dto.HitAddDto;
import ru.practicum.dto.HitDto;
import ru.practicum.model.EndpointHit;

import java.util.Arrays;
import java.util.List;

public interface StatService {
    EndpointHit addHit(HitAddDto hitAddDto);

    List<EndpointHit> getStats(String start, String end);

    HitDto getUriStats(String start, String end, String uri);
}
