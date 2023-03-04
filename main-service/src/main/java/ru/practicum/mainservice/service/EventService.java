package ru.practicum.mainservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.event.EventShortResponse;
import ru.practicum.mainservice.dto.event.FullEventResponse;
import ru.practicum.mainservice.dto.event.NewEventRequest;
import ru.practicum.mainservice.dto.event.UpdateEventRequest;
import ru.practicum.mainservice.errorHandler.StartTimeEventException;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.model.CategoryEntity;
import ru.practicum.mainservice.model.EventEntity;
import ru.practicum.mainservice.model.EventShortEntity;
import ru.practicum.mainservice.model.UserEntity;
import ru.practicum.mainservice.model.eventStateMachine.EventAction;
import ru.practicum.mainservice.model.eventStateMachine.EventState;
import ru.practicum.mainservice.model.eventStateMachine.StateMachine;
import ru.practicum.mainservice.repository.EventRepository;

import java.time.LocalDateTime;
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
        checkEventDateStartTime(request.getEventDate());
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        CategoryEntity category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        EventEntity newEventEntity = eventMapper.entityFromNewRequest(request, user, category);
        StateMachine machine = new StateMachine(EventState.CREATED);
        machine.getEventState().sentToReview(machine);
        newEventEntity.setState(machine.getEventState());
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
        checkIsInitiatorEvent(userId, entity);
        return eventMapper.responseFromEntity(entity);
    }

    @Transactional
    public FullEventResponse updateUserEvent(Long userId, Long eventId, UpdateEventRequest request) {
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        EventEntity event = checkEventIsExistAndGet(eventId);
        checkIsInitiatorEvent(userId, event);

        CategoryEntity category = null;
        if (request.getCategory() != null) {
            category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        }

        EventEntity updateFields = eventMapper.entityFromUpdateRequest(request, category);
        eventMapper.updateEntity(updateFields, event);
        checkEventDateStartTime(event.getEventDate());

        if (request.getStateAction() != null) {
            EventState state = event.getState();
            StateMachine machine = new StateMachine(state);

            EventAction action = EventAction.valueOf(request.getStateAction());
            switch (action) {
                case SEND_TO_REVIEW:
                    state.sentToReview(machine);
                    break;
                case CANCEL_REVIEW:
                    state.cancelReview(machine);
                    break;
                default:
                    throw new IllegalArgumentException("'state action' " + action + " not avaliable for user");
            }
            event.setState(machine.getEventState());
        }

        ///rule update state

        return eventMapper.responseFromEntity(event);
    }

    public EventEntity checkEventIsExistAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " was not found"));
    }

    private void checkIsInitiatorEvent(Long userId, EventEntity event) {
        if (!event.getInitiator().getUserId().equals(userId)) {
            throw new NoSuchElementException("Event with id=" + event.getEventId() + " was not found");
        }
    }

    private void checkEventDateStartTime(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new StartTimeEventException(eventDate.toString());
        }
    }
}
