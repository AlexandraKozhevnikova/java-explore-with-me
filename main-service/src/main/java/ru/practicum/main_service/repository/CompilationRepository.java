package ru.practicum.main_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.model.CompilationEntity;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {
}
