package ru.practicum.main_service.repository;

import ru.practicum.main_service.model.CategoryEntity;

import java.util.List;

public interface CategoryCustomRepository {

    List<CategoryEntity> getCategories(Integer from, Integer size);
}
