package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.main_service.dto.NewUserRequest;
import ru.practicum.main_service.dto.UserResponse;
import ru.practicum.main_service.model.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    UserEntity entityFromNewUserRequest(NewUserRequest request);

    @Mapping(target = "id", source = "userId")
    UserResponse responseDtoFromEntity(UserEntity user);
}
