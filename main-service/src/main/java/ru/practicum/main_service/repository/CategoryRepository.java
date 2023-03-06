package ru.practicum.main_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.main_service.model.CategoryEntity;
import ru.practicum.main_service.model.UserEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, QuerydslPredicateExecutor<UserEntity>,
        CategoryCustomRepository {
}
