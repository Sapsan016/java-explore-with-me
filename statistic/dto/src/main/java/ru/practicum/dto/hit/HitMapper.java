package ru.practicum.dto.hit;
import ru.practicum.dto.hit.HitAddDto;
import ru.practicum.dto.hit.HitDto;
import ru.practicum.model.EndpointHit;

public class HitMapper {
    public static EndpointHit toHit(HitAddDto hitAddDto) {
        return new EndpointHit(
                null,
                hitAddDto.getApp(),
                hitAddDto.getUri(),
                hitAddDto.getIp(),
                hitAddDto.getTimestamp()
        );
    }

    public static HitDto toHitDto(EndpointHit endpointHit) {
        return new HitDto(
                endpointHit.getApp(),
                endpointHit.getUri(),
                null
        );
    }
}
