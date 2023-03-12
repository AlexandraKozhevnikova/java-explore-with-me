package ru.practicum.main_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.main_service.dto.event.EventFullResponse;
import ru.practicum.main_service.dto.event.EventShortResponse;
import ru.practicum.main_service.dto.event.NewEventRequest;
import ru.practicum.main_service.dto.event.UpdateEventRequest;
import ru.practicum.main_service.model.CategoryEntity;
import ru.practicum.main_service.model.CurrencyEntity;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.EventShortEntity;
import ru.practicum.main_service.model.UserEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = CategoryMapper.class)
public interface EventMapper {

    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "amount", source = "request.amount.total", defaultValue = "0")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "moderationRequired", source = "request.requestModeration", defaultValue = "true")
    @Mapping(target = "lon", source = "request.location.lon")
    @Mapping(target = "lat", source = "request.location.lat")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "paid", source = "request.paid", defaultValue = "false")
    @Mapping(target = "participantLimit", source = "request.participantLimit", defaultValue = "0")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EventEntity entityFromNewRequest(
            NewEventRequest request,
            UserEntity user,
            CategoryEntity category,
            CurrencyEntity currency
    );

    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "amount", ignore = true)
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
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "views", constant = "0L")
    @Mapping(target = "amount.currency", source = "currency.title")
    @Mapping(target = "amount.total", source = "amount")
    EventFullResponse responseFromEntity(EventEntity entity);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "id", source = "eventId")
    @Mapping(target = "category.id", source = "category.catId")
    @Mapping(target = "initiator.id", source = "initiator.userId")
    EventShortResponse shortResponseFromShortEntity(EventShortEntity entity);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "id", source = "eventId")
    @Mapping(target = "views", ignore = true)
    EventShortResponse shortResponseFromFullEntity(EventEntity entity);
}
