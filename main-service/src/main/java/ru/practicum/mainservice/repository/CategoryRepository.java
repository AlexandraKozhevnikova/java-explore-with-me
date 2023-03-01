package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
