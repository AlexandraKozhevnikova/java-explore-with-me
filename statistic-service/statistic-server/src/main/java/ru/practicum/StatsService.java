package ru.practicum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.AppEntity;
import ru.practicum.model.HitEntity;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.HitRepository;
import statisticcommon.HitRequest;
import statisticcommon.HitResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private final HitRepository hitRepository;
    private final AppRepository appRepository;
    private final HitMapper mapper;
    private final Logger log = LoggerFactory.getLogger(StatsService.class);

    public StatsService(HitRepository hitRepository, AppRepository appRepository, HitMapper mapper) {
        this.hitRepository = hitRepository;
        this.appRepository = appRepository;
        this.mapper = mapper;
    }

    @Transactional
    public void createHit(HitRequest body) {
        AppEntity app = createAppIfNotExist(body.getApp());
        HitEntity hit = mapper.entityFromDto(body, app);
        hitRepository.save(hit);
    }

    @Transactional
    public AppEntity createAppIfNotExist(String name) {
        Optional<AppEntity> app = Optional.ofNullable(appRepository.getAppEntityByName(name));
        return app.orElseGet(() -> {
            log.info("created new app: {}", app);
            return appRepository.save(new AppEntity(name));
        });
    }

    @Transactional(readOnly = true)
    public List<HitResponse> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<HitResponse> response = hitRepository.getSummaryHits(start, end, uris, unique);

        uris.stream()
                .filter(it -> !response
                        .stream()
                        .map(HitResponse::getUri)
                        .collect(Collectors.toList())
                        .contains(it)
                )
                .forEach(it -> response.add(new HitResponse("unknownAppName", it, 0L)));

        return response;
    }
}
