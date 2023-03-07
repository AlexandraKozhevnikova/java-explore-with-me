package ru.practicum.main_service.repository;

import com.querydsl.core.BooleanBuilder;
import ru.practicum.main_service.model.CompilationEntity;

import java.util.List;

public interface CompilationCustomRepository {
    List<CompilationEntity> getCompilations(BooleanBuilder booleanBuilder, Integer from, Integer size);

    CompilationEntity getEnrichedCompilation(Long compId);
}
