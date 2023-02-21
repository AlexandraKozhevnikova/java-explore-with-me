package ru.practicum.all;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.HitEntity;

public interface StatsRepository extends JpaRepository<HitEntity, Long>, QuerydslPredicateExecutor<HitEntity> {
}
