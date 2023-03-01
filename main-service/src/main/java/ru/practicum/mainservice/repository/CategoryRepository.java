package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.model.UserEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, QuerydslPredicateExecutor<UserEntity>,
CategoryCustomRepository {
}
