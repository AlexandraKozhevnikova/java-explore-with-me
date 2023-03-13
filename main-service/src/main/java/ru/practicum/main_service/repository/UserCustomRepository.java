package ru.practicum.main_service.repository;

import ru.practicum.main_service.model.UserEntity;

import java.util.List;

public interface UserCustomRepository {

    List<UserEntity> getUsers(List<Long> ids, Integer from, Integer size);
}
