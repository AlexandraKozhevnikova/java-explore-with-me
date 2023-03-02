package ru.practicum.mainservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainservice.dto.FullEventResponse;
import ru.practicum.mainservice.dto.NewEventRequest;
import ru.practicum.mainservice.dto.UpdateEvenRequest;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.model.EventEntity;
import ru.practicum.mainservice.model.UserEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EventMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "lon", source = "location.lon")
    @Mapping(target = "lat", source = "location.lat")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    EventEntity entityFromNewRequest(NewEventRequest request, UserEntity user, CategoryEntity category);

    EventEntity entityFromUpdateRequest(UpdateEvenRequest request);

    FullEventResponse responseFromEntity(EventEntity entity);
}
