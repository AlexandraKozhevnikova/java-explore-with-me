package ru.practicum.mainservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.mainservice.dto.event.EventFullResponse;
import ru.practicum.mainservice.dto.event.EventShortResponse;
import ru.practicum.mainservice.model.EventEntity;
import ru.practicum.mainservice.model.EventShortEntity;
import ru.practicum.mainservice.model.QCategoryEntity;
import ru.practicum.mainservice.model.QEventEntity;
import ru.practicum.mainservice.model.QUserEntity;
import ru.practicum.mainservice.model.eventStateMachine.EventState;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventCustomRepositoryImpl implements EventCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EventShortEntity> getUserEvents(Long userId, Integer from, Integer size) {
        JPAQuery<?> query = new JPAQuery(entityManager);

        QEventEntity event = QEventEntity.eventEntity;
        return query
                .select(Projections.constructor(EventShortEntity.class, event.eventId, event.title, event.category,
                        event.annotation, event.eventDate, event.isPaid, event.initiator))
                .from(event)
                .where(event.initiator.userId.eq(userId))
                .offset(from)
                .limit(size)
                .fetch();
    }

    @Override
    public List<EventEntity> getEventsForAdmin(BooleanExpression byUserIds,
                                               BooleanExpression byStates,
                                               BooleanExpression byCategoryIds,
                                               BooleanExpression byRangeStart,
                                               BooleanExpression byRangeEnd,
                                               Integer from,
                                               Integer size) {
        JPAQuery<?> query = new JPAQuery(entityManager);
        QEventEntity event = QEventEntity.eventEntity;

        return query
                .select(event)
                .from(event)
                .where(byUserIds)
                .where(byStates)
                .where(byCategoryIds)
                .where(byRangeStart)
                .where(byRangeEnd)
                .leftJoin(QUserEntity.userEntity)
                .on(event.initiator.userId.eq(QUserEntity.userEntity.userId))
                .leftJoin(QCategoryEntity.categoryEntity)
                .on(event.category.catId.eq(QCategoryEntity.categoryEntity.catId))
                .offset(from)
                .limit(size)
                .fetch();
    }


}
