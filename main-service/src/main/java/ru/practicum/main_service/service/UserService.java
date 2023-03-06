package ru.practicum.main_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.NewUserRequest;
import ru.practicum.main_service.dto.UserResponse;
import ru.practicum.main_service.mapper.UserMapper;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse createUser(NewUserRequest userRequest) {
        UserEntity user = userRepository.save(userMapper.entityFromNewUserRequest(userRequest));
        return userMapper.responseDtoFromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(List<Long> ids, Integer from, Integer size) {
        return userRepository.getUsers(ids, from, size).stream()
                .map(userMapper::responseDtoFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        checkUserIsExistAndGetById(userId);
        userRepository.deleteById(userId);
    }

    public UserEntity checkUserIsExistAndGetById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id=" + userId + " was not found"));
    }

    public List<UserEntity> checkListUsersIsExist(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }
}
