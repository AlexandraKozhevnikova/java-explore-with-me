package ru.practicum.mainservice.service;

import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.FullEventResponse;
import ru.practicum.mainservice.dto.NewEventRequest;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.model.EventEntity;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.UserEntity;
import ru.practicum.mainservice.repository.EventRepository;

@Service
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;


    public EventService(EventMapper eventMapper, EventRepository eventRepository, UserService userService,
                        CategoryService categoryService) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public FullEventResponse createEvent(Long userId, NewEventRequest request) {
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        CategoryEntity category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        EventEntity newEventEntity = eventMapper.entityFromNewRequest(request, user, category);
        newEventEntity.setState(EventState.CREATED);
        EventEntity event = eventRepository.save(newEventEntity);
        return eventMapper.responseFromEntity(event);
    }
}
