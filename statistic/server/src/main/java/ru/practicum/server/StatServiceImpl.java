package ru.practicum.server;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitAddDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitMapper;
import ru.practicum.exception.HitsNotFoundException;
import ru.practicum.model.EndpointHit;

import java.util.List;

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
        log.info("Добавлена информация о запросе с Id = {}", hit.getId());
        return hit;
    }


    @Override
    public HitDto getUriStats(String start, String end, String uri) {
        log.info("Запрошена статистика за период с {} по {} для uri = {}", start, end, uri);
        List<EndpointHit> list = statRepository.findAllByTimestampBetweenAndUri(start, end, uri);
        return addHitCount(list);
    }

    @Override
    public HitDto getUniqueIpStats(String start, String end, String uri) {
        log.info("Запрошена статистика за период с {} по {} для uri = {} c уникальными IP", start, end, uri);
        List<EndpointHit> list = statRepository.findUniqueUriStats(uri, start, end);
        return addHitCount(list);
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
