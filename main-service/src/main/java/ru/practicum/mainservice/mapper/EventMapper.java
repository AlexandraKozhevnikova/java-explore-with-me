package ru.practicum.mainservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.mainservice.dto.event.EventShortResponse;
import ru.practicum.mainservice.dto.event.FullEventResponse;
import ru.practicum.mainservice.dto.event.NewEventRequest;
import ru.practicum.mainservice.dto.event.UpdateEventRequest;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.model.EventEntity;
import ru.practicum.mainservice.model.EventShortEntity;
import ru.practicum.mainservice.model.UserEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EventMapper {

    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "moderationRequired", source = "request.requestModeration")
    @Mapping(target = "lon", source = "request.location.lon")
    @Mapping(target = "lat", source = "request.location.lat")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "user")
    EventEntity entityFromNewRequest(
            NewEventRequest request,
            UserEntity user,
            CategoryEntity category
    );

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "moderationRequired", source = "request.requestModeration")
    @Mapping(target = "lon", source = "request.location.lon")
    @Mapping(target = "lat", source = "request.location.lat")
    @Mapping(target = "category", source = "category")
    EventEntity entityFromUpdateRequest(UpdateEventRequest request, CategoryEntity category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(EventEntity request, @MappingTarget EventEntity entity);

    @Mapping(target = "location", ignore = true)
    @Mapping(target = "id", source = "eventId")
    @Mapping(target = "requestModeration", source = "moderationRequired")
    @Mapping(target = "location.lon", source = "lon")
    @Mapping(target = "location.lat", source = "lat")
    @Mapping(target = "category.id", source = "category.catId")
    @Mapping(target = "initiator.id", source = "initiator.userId")
    FullEventResponse responseFromEntity(EventEntity entity);

    @Mapping(target = "id", source = "eventId")
    @Mapping(target = "category.id", source = "category.catId")
    @Mapping(target = "initiator.id", source = "initiator.userId")
    EventShortResponse shortResponseFromShortEntity(EventShortEntity entity);
}
