package ru.practicum.mainservice.repository;

import ru.practicum.mainservice.model.CategoryEntity;

import java.util.List;

public interface CategoryCustomRepository {

    List<CategoryEntity> getCategories(Integer from, Integer size);
}
