package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.dto.CategoryRequest;
import ru.practicum.main_service.dto.CategoryResponse;
import ru.practicum.main_service.model.CategoryEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CategoryMapper {
    @Mapping(target = "catId", ignore = true)
    CategoryEntity entityFromNewRequest(CategoryRequest request);

    @Mapping(target = "id", source = "catId")
    CategoryResponse responseFromEntity(CategoryEntity entity);
}
