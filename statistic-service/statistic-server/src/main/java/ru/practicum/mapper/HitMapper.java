package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.AppEntity;
import ru.practicum.model.HitEntity;
import statisticcommon.HitRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateTime", source = "request.timestamp")
    @Mapping(target = "app", source = "appEntity")
    HitEntity entityFromDto(HitRequest request, AppEntity appEntity);
}
