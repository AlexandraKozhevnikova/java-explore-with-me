package ru.practicum.all;

import org.springframework.stereotype.Service;
import ru.practicum.dto.HitMapper;
import ru.practicum.dto.HitRequest;
import ru.practicum.model.HitEntity;

@Service
public class StatsService {
    private final StatsRepository repository;
    private final HitMapper mapper;

    public StatsService(StatsRepository repository, HitMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public void createHit(HitRequest body){
        HitEntity hit = mapper.entityFromDto(body);
        repository.save(hit);
    }
}
