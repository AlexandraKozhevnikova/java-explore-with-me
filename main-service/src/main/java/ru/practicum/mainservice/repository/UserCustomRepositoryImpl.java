package ru.practicum.mainservice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.mainservice.model.QUserEntity;
import ru.practicum.mainservice.model.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.TRUE;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserEntity> getUsers(List<Long> ids, Integer from, Integer size) {
        BooleanExpression byId;

        if (ids == null || ids.isEmpty()) {
            byId = TRUE.isTrue();
        } else {
            byId = QUserEntity.userEntity.userId.in(ids);
        }

        JPAQuery<?> query = new JPAQuery(entityManager);
        return query.select(QUserEntity.userEntity)
            .from(QUserEntity.userEntity)
            .where(byId)
            .offset(from)
            .limit(size)
            .fetch();
    }
}
