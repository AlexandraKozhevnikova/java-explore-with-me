package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.dto.RequestResponse;
import ru.practicum.main_service.model.RequestEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RequestMapper {
    @Mapping(target = "participantId", source = "participant.userId")
    @Mapping(target = "eventId", source = "event.eventId")
    RequestResponse responseFromEntity(RequestEntity request);
}
