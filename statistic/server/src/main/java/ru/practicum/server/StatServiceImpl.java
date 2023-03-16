package ru.practicum.server;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitAddDto;
import ru.practicum.dto.HitMapper;
import ru.practicum.model.EndpointHit;

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
        log.info("Добавлена информация о запросе с Id = {} создан", hit.getId());
        return hit;
    }
}
