package ru.practicum.mainservice.repository;

import ru.practicum.mainservice.model.EventShortEntity;

import java.util.List;

public interface EventCustomRepository {

    List<EventShortEntity> getUserEvents(Long userId, Integer from, Integer size);
}
