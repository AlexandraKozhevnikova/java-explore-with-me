package ru.practicum.mainservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainservice.dto.CategoryRequest;
import ru.practicum.mainservice.dto.CategoryResponse;
import ru.practicum.mainservice.model.CategoryEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CategoryMapper {
    @Mapping(target = "catId", ignore = true)
    CategoryEntity entityFromNewRequest(CategoryRequest request);

    @Mapping(target = "id", source = "catId")
    CategoryResponse responseFromEntity(CategoryEntity entity);
}
