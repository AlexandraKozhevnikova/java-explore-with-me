package ru.practicum.mainservice.repository;

import ru.practicum.mainservice.model.UserEntity;

import java.util.List;

public interface UserCustomRepository {

    List<UserEntity> getUsers(List<Long> ids, Integer from, Integer size);
}
