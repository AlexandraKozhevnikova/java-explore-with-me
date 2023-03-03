package ru.practicum.mainservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.event.EventShortResponse;
import ru.practicum.mainservice.dto.event.FullEventResponse;
import ru.practicum.mainservice.dto.event.NewEventRequest;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.model.EventEntity;
import ru.practicum.mainservice.model.EventShortEntity;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.UserEntity;
import ru.practicum.mainservice.repository.EventRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    @Transactional
    public FullEventResponse createEvent(Long userId, NewEventRequest request) {
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        CategoryEntity category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        EventEntity newEventEntity = eventMapper.entityFromNewRequest(request, user, category);
        newEventEntity.setState(EventState.CREATED);
        EventEntity event = eventRepository.save(newEventEntity);
        return eventMapper.responseFromEntity(event);
    }

    @Transactional(readOnly = true)
    public List<EventShortResponse> getUserEvents(Long userId, Integer from, Integer size) {
        userService.checkUserIsExistAndGetById(userId);

        List<EventShortEntity> events = eventRepository.getUserEvents(userId, from, size);

        return events.stream()
                .map(eventMapper::shortResponseFromShortEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FullEventResponse getUserEventById(Long userId, Long eventId) {
        userService.checkUserIsExistAndGetById(userId);
        EventEntity entity = checkEventIsExistAndGet(eventId);
        if (!entity.getInitiator().getUserId().equals(userId)) {
            throw new NoSuchElementException("Event with id=" + eventId + " was not found");
        }
        return eventMapper.responseFromEntity(entity);
    }

    public EventEntity checkEventIsExistAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " was not found"));
    }
}
