package ru.practicum.main_service.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.EventShortEntity;

import java.util.Collection;
import java.util.List;

public interface EventCustomRepository {

    List<EventShortEntity> getUserEvents(Long userId, Integer from, Integer size);

    List<EventShortEntity> getPublishedEvent(BooleanBuilder booleanBuilder,
                                             OrderSpecifier<?> orderBy,
                                             Integer from,
                                             Integer size);

    List<EventEntity> getEventsForAdmin(BooleanBuilder booleanBuilder, Integer from, Integer size);

    List<EventEntity> getListEvents(Collection<Long> eventIds);
}

