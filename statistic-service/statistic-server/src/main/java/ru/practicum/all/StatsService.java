package ru.practicum.all;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.HitEntity;
import ru.practicum.model.QHitEntity;
import statisticcommon.HitRequest;
import statisticcommon.HitResponse;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private final StatsRepository repository;
    private final HitMapper mapper;

    private final EntityManager entityManager;

    public StatsService(StatsRepository repository, HitMapper mapper, EntityManager entityManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Transactional
    public void createHit(HitRequest body) {
        HitEntity hit = mapper.entityFromDto(body);
        repository.save(hit);
    }

    @Transactional(readOnly = true)
    public List<HitResponse> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        QHitEntity hit = QHitEntity.hitEntity;

        BooleanExpression byUri;
        if (uris == null || uris.isEmpty()) {
            byUri = Expressions.TRUE.isTrue();
        } else {
            byUri = hit.uri.in(uris);
        }

        NumberPath<Long> aliasCount = Expressions.numberPath(Long.class, "aliasCount");

        SimpleExpression<?> distinctIpPredicate = unique
            ? hit.ip.countDistinct().as(aliasCount)
            : hit.ip.count().as(aliasCount);

        JPAQueryFactory query = new JPAQueryFactory(entityManager);

        return query
            .select(hit.app, hit.uri, distinctIpPredicate)
            .from(hit)
            .where(hit.dateTime.between(start, end))
            .where(byUri)
            .groupBy(hit.app, hit.uri)
            .orderBy(aliasCount.desc())
            .fetch()
            .stream()
            .map(it -> new HitResponse(
                it.get(hit.app),
                it.get(hit.uri),
                it.get(aliasCount)
            ))
            .collect(Collectors.toList());
    }
}
