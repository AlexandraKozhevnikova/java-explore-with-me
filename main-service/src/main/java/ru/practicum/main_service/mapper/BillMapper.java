package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.dto.BillResponse;
import ru.practicum.main_service.model.BillEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {UserMapper.class})
public interface BillMapper {

    @Mapping(target = "id", source = "billId")
    @Mapping(target = "amount.total", source = "amount")
    @Mapping(target = "amount.currency", source = "currency.title")
    @Mapping(target = "event.id", source = "event.eventId")
    @Mapping(target = "event.initiator.id", source = "event.initiator.userId")
    @Mapping(target = "event.amount.currency", source = "event.currency.title")
    @Mapping(target = "event.amount.total", source = "event.amount")
    BillResponse responseFromEntity(BillEntity billEntity);
}
