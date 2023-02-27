package ru.practicum.mainservice.service;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.NewUserRequest;
import ru.practicum.mainservice.dto.UserResponse;
import ru.practicum.mainservice.mapper.UserMapper;
import ru.practicum.mainservice.model.UserEntity;
import ru.practicum.mainservice.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(NewUserRequest userRequest) {
        UserEntity user = userRepository.save(userMapper.entityFromNewUserRequest(userRequest));
        return userMapper.responseDtoFromEntity(user);
    }
}
