package ru.practicum.mainservice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import ru.practicum.mainservice.dto.event.EventFullResponse;
import ru.practicum.mainservice.dto.event.EventShortResponse;
import ru.practicum.mainservice.model.EventEntity;
import ru.practicum.mainservice.model.EventShortEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCustomRepository {

    List<EventShortEntity> getUserEvents(Long userId, Integer from, Integer size);

    List<EventEntity> getEventsForAdmin(BooleanExpression byUserIds,
                                        BooleanExpression byStates,
                                        BooleanExpression byCategoryIds,
                                        BooleanExpression byRangeStart,
                                        BooleanExpression byRangeEnd,
                                        Integer from,
                                        Integer size);
}
