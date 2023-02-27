package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.HitEntity;

public interface HitRepository extends JpaRepository<HitEntity, Long>, QuerydslPredicateExecutor<HitEntity>,
    CustomStatsRepository {
}
