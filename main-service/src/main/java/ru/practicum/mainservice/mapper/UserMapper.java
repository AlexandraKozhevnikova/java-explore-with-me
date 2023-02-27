package ru.practicum.mainservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.mainservice.dto.NewUserRequest;
import ru.practicum.mainservice.dto.UserResponse;
import ru.practicum.mainservice.model.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    UserEntity entityFromNewUserRequest(NewUserRequest request);

    @Mapping(target = "id", source = "userId")
    UserResponse responseDtoFromEntity(UserEntity user);
}
