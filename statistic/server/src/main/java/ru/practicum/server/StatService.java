package ru.practicum.server;

import ru.practicum.dto.HitAddDto;
import ru.practicum.model.EndpointHit;

public interface StatService {
    EndpointHit addHit(HitAddDto hitAddDto);
}
