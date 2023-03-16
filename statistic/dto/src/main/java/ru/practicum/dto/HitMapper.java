package ru.practicum.dto;
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
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }
}
