package ru.practicum.main_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.main_service.model.RequestEntity;

public interface RequestRepository extends JpaRepository<RequestEntity, Long>,
        QuerydslPredicateExecutor<RequestEntity> {
}
