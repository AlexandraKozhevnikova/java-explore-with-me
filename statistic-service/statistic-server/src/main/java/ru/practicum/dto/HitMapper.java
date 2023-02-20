package ru.practicum.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.HitEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HitMapper {

    @Mapping(target = "dateTime", source = "timestamp")
    HitEntity entityFromDto(HitRequest request);
}
