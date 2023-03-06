package ru.practicum.main_service.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.EventShortEntity;

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

    List<EventShortEntity> getPublishedEvent(BooleanExpression byText,
                                             BooleanExpression byCategoryIds,
                                             BooleanExpression byIsPaid,
                                             BooleanExpression byIsOnlyAvailable,
                                             BooleanExpression byEventDate,
                                             OrderSpecifier<?> orderBy,
                                             Integer from,
                                             Integer size);
}
