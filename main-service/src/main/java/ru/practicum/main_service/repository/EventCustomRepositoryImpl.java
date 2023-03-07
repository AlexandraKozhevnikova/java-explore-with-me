package ru.practicum.main_service.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.EventShortEntity;
import ru.practicum.main_service.model.QCategoryEntity;
import ru.practicum.main_service.model.QEventEntity;
import ru.practicum.main_service.model.QUserEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
    public List<EventShortEntity> getPublishedEvent(BooleanBuilder booleanBuilder, OrderSpecifier<?> orderBy,
                                                    Integer from, Integer size) {
        JPAQuery<?> query = new JPAQuery(entityManager);
        QEventEntity event = QEventEntity.eventEntity;

        return query
                .select(Projections.constructor(EventShortEntity.class, event.eventId, event.title, event.category,
                        event.annotation, event.eventDate, event.isPaid, event.initiator))
                .from(event)
                .where(booleanBuilder)
                .orderBy(orderBy)
                .offset(from)
                .limit(size)
                .fetch();
    }

    @Override
    public List<EventEntity> getEventsForAdmin(BooleanBuilder booleanBuilder, Integer from, Integer size) {
        JPAQuery<?> query = new JPAQuery(entityManager);
        QEventEntity event = QEventEntity.eventEntity;

        return query
                .select(event)
                .from(event)
                .where(booleanBuilder)
                .leftJoin(QUserEntity.userEntity)
                .on(event.initiator.userId.eq(QUserEntity.userEntity.userId))
                .leftJoin(QCategoryEntity.categoryEntity)
                .on(event.category.catId.eq(QCategoryEntity.categoryEntity.catId))
                .offset(from)
                .limit(size)
                .fetch();
    }
}
