package ru.practicum.main_service.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.main_service.model.CompilationEntity;
import ru.practicum.main_service.model.QCategoryEntity;
import ru.practicum.main_service.model.QCompilationEntity;
import ru.practicum.main_service.model.QEventEntity;
import ru.practicum.main_service.model.QUserEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CompilationCustomRepositoryImpl implements CompilationCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CompilationEntity> getCompilations(BooleanBuilder booleanBuilder, Integer from, Integer size) {

        JPAQuery<?> query = new JPAQuery(entityManager);

        return query
                .select(QCompilationEntity.compilationEntity)
                .from(QCompilationEntity.compilationEntity)
                .where(booleanBuilder)
                .leftJoin(QCompilationEntity.compilationEntity.events, QEventEntity.eventEntity)
                .fetchJoin()
                .leftJoin(QEventEntity.eventEntity.initiator, QUserEntity.userEntity)
                .fetchJoin()
                .leftJoin(QEventEntity.eventEntity.category, QCategoryEntity.categoryEntity)
                .fetchJoin()
                .distinct()
                .fetch();
    }

    @Override
    public CompilationEntity getEnrichedCompilation(Long compId) {
        JPAQuery<?> query = new JPAQuery(entityManager);

        return query
                .select(QCompilationEntity.compilationEntity)
                .from(QCompilationEntity.compilationEntity)
                .where(QCompilationEntity.compilationEntity.compilationId.eq(compId))
                .leftJoin(QCompilationEntity.compilationEntity.events, QEventEntity.eventEntity)
                .fetchJoin()
                .leftJoin(QEventEntity.eventEntity.initiator, QUserEntity.userEntity)
                .fetchJoin()
                .leftJoin(QEventEntity.eventEntity.category, QCategoryEntity.categoryEntity)
                .fetchJoin()
                .fetchOne();
    }
}
