package ru.practicum.mainservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.mainservice.model.EventShortEntity;
import ru.practicum.mainservice.model.QEventEntity;

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
}
