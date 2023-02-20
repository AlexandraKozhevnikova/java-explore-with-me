package ru.practicum.all;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.HitEntity;

public interface StatsRepository extends JpaRepository<HitEntity, Long> {
}
