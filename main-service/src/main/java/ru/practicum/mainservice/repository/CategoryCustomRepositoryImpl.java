package ru.practicum.mainservice.repository;

import com.querydsl.jpa.impl.JPAQuery;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.model.QCategoryEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CategoryEntity> getCategories(Integer from, Integer size) {
        JPAQuery<?> query = new JPAQuery(entityManager);

        return query.select(QCategoryEntity.categoryEntity)
                .from(QCategoryEntity.categoryEntity)
                .offset(from)
                .limit(size)
                .fetch();
    }
}
