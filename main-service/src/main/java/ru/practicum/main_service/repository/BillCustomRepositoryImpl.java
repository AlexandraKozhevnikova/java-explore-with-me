package ru.practicum.main_service.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.main_service.model.BillState;
import ru.practicum.main_service.model.QBillEntity;
import ru.practicum.main_service.model.QEventEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class BillCustomRepositoryImpl implements BillCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tuple> getEventPaymentsReport(Long initiatorId) {
        JPAQuery<?> query = new JPAQuery(entityManager);

        QEventEntity event = QEventEntity.eventEntity;

        return query
                .select(event.eventId, event.amount.sum(), QBillEntity.billEntity.count(), event.amount.avg())
                .from(event)
                .leftJoin(QBillEntity.billEntity)
                .on(event.eventId.eq(QBillEntity.billEntity.event.eventId))
                .where(event.isPaid.isTrue())
                .where(event.amount.gt(0))
                .where(QBillEntity.billEntity.state.eq(BillState.PAID))
                .fetchJoin()
                .groupBy(event)
                .fetch();
    }
}
