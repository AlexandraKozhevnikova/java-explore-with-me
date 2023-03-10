package ru.practicum.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.model.QHitEntity;
import statisticcommon.HitResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomStatsRepositoryImpl implements CustomStatsRepository {
    private final JPAQueryFactory query;

    public CustomStatsRepositoryImpl(JPAQueryFactory query) {
        this.query = query;
    }

    public List<HitResponse> getSummaryHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        QHitEntity hit = QHitEntity.hitEntity;

        BooleanBuilder booleanBuilder = new BooleanBuilder(hit.dateTime.between(start, end));

        if (uris != null && !uris.isEmpty()) {
            booleanBuilder.and(hit.uri.in(uris));
        }

        NumberPath<Long> aliasCount = Expressions.numberPath(Long.class, "aliasCount");

        SimpleExpression<?> distinctIpPredicate = unique
                ? hit.ip.countDistinct().as(aliasCount)
                : hit.ip.count().as(aliasCount);

        return query
                .select(hit.app.name, hit.uri, distinctIpPredicate)
                .from(hit)
                .where(booleanBuilder)
                .groupBy(hit.app.name, hit.uri)
                .orderBy(aliasCount.desc())
                .fetch()
                .stream()
                .map(it -> new HitResponse(
                        it.get(hit.app.name),
                        it.get(hit.uri),
                        it.get(aliasCount)
                ))
                .collect(Collectors.toList());
    }
}
