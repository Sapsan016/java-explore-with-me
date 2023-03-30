package ru.practicum.server;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.hit.HitAddDto;
import ru.practicum.dto.hit.HitDto;
import ru.practicum.dto.hit.HitMapper;
import ru.practicum.exception.HitsNotFoundException;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StatServiceImpl implements StatService {

    StatRepository statRepository;

    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }


    @Override
    public EndpointHit addHit(HitAddDto hitAddDto) {
        EndpointHit hit = HitMapper.toHit(hitAddDto);
        statRepository.save(hit);
        log.info("Добавлена информация о запросе с ID = {}", hit.getId());
        return hit;
    }


    @Override
    public HitDto getUriStats(LocalDateTime start, LocalDateTime end, String uri) {
        log.info("Запрошена статистика за период с {} по {} для URI = {}", start, end, uri);
        List<EndpointHit> list = statRepository.findAllByTimestampBetweenAndUri(start, end, uri);
        return addHitCount(list);
    }

    @Override
    public HitDto getUniqueIpStats(LocalDateTime start, LocalDateTime end, String uri) {
        log.info("Запрошена статистика за период с {} по {} для uri = {} c уникальными URI", start, end, uri);
        List<EndpointHit> list = statRepository.findUniqueUriStats(uri, start, end);
        return addHitCount(list);
    }

    @Override
    public List<HitDto> getAllStats(LocalDateTime start, LocalDateTime end) {
        log.info("Запрошена вся статистика за период с {} по {} ", start, end );
        List<EndpointHit> list = statRepository.findAllByTimestampBetween(start, end);
        int hits = list.size();
        return list.stream().map(HitMapper::toHitDto).peek(hitDto -> hitDto.setHits(hits)).collect(Collectors.toList());
    }

    private HitDto addHitCount(List<EndpointHit> list) {
        int hits = list.size();
        if (list.isEmpty()) {
            throw new HitsNotFoundException("Сведения отсуствуют");
        }
        HitDto dto = HitMapper.toHitDto(list.get(0));
        dto.setHits(hits);
        return dto;
    }
}
