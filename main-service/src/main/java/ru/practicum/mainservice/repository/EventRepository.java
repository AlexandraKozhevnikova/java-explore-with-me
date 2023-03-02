package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainservice.model.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity, Long>, QuerydslPredicateExecutor<EventEntity> {
}
