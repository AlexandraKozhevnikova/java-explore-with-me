package ru.practicum.main_service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.event.EventFullResponse;
import ru.practicum.main_service.dto.event.EventShortResponse;
import ru.practicum.main_service.dto.event.NewEventRequest;
import ru.practicum.main_service.dto.event.UpdateEventRequest;
import ru.practicum.main_service.errorHandler.IllegalStateEventException;
import ru.practicum.main_service.errorHandler.StartTimeEventException;
import ru.practicum.main_service.mapper.EventMapper;
import ru.practicum.main_service.model.CategoryEntity;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.EventShortEntity;
import ru.practicum.main_service.model.QEventEntity;
import ru.practicum.main_service.model.QRequestEntity;
import ru.practicum.main_service.model.RequestState;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.model.eventStateMachine.EventAction;
import ru.practicum.main_service.model.eventStateMachine.EventState;
import ru.practicum.main_service.model.eventStateMachine.StateMachine;
import ru.practicum.main_service.repository.EventRepository;
import ru.practicum.statisticclient.StatisticClient;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final RequestService requestService;
    private final StatisticClient client;
    private static final Logger log = LogManager.getLogger(EventService.class);


    public EventService(EventMapper eventMapper, EventRepository eventRepository, UserService userService,
                        CategoryService categoryService, @Lazy RequestService requestService, StatisticClient client) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.requestService = requestService;
        this.client = client;
    }

    @Transactional
    public EventFullResponse createEvent(Long userId, NewEventRequest request) {
        checkEventDateStartTime(request.getEventDate(), 2L);
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

        Map<Long, Long> requests = requestService.getCountRequestsByListEventIds(
                events.stream()
                        .map(EventShortEntity::getEventId)
                        .collect(Collectors.toList())
        );

        return events.stream()
                .map(eventMapper::shortResponseFromShortEntity)
                .peek(it -> it.setConfirmedRequest(requests.getOrDefault(it.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullResponse getUserEventById(Long userId, Long eventId) {
        userService.checkUserIsExistAndGetById(userId);
        EventEntity entity = checkEventIsExistAndGet(eventId);
        checkUserIsEventInitiator(userId, entity);
        EventFullResponse response = eventMapper.responseFromEntity(entity);
        response.setConfirmedRequest(requestService.getParticipantCountForEvent(eventId));
        return response;
    }

    @Transactional
    public EventFullResponse updateUserEvent(Long userId, Long eventId, UpdateEventRequest request) {
        userService.checkUserIsExistAndGetById(userId);
        EventEntity event = checkEventIsExistAndGet(eventId);
        checkEventDateStartTime(event.getEventDate(), 2L);
        checkUserIsEventInitiator(userId, event);
        checkThatEventIsAvailableForUpdate(event);

        CategoryEntity category = null;
        if (request.getCategory() != null) {
            category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        }

        EventEntity updateFields = eventMapper.entityFromUpdateRequest(request, category);
        eventMapper.updateEntity(updateFields, event);
        checkEventDateStartTime(event.getEventDate(), 2L);

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
                    throw new IllegalArgumentException("'state action' " + action + " not available for user");
            }
            event.setState(machine.getEventState());
        }

        EventFullResponse response = eventMapper.responseFromEntity(event);
        response.setConfirmedRequest(requestService.getParticipantCountForEvent(eventId));
        return response;
    }

    @Transactional
    public EventFullResponse updateUserEventForAdmin(Long eventId, UpdateEventRequest request) {
        EventEntity event = checkEventIsExistAndGet(eventId);
        checkEventDateStartTime(event.getEventDate(), 1L);
        checkThatEventIsAvailableForUpdate(event);

        CategoryEntity category = null;
        if (request.getCategory() != null) {
            category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        }

        EventEntity updateFields = eventMapper.entityFromUpdateRequest(request, category);
        eventMapper.updateEntity(updateFields, event);
        checkEventDateStartTime(event.getEventDate(), 1L);

        if (request.getStateAction() != null) {
            EventState state = event.getState();
            StateMachine machine = new StateMachine(state);

            EventAction action = EventAction.valueOf(request.getStateAction());
            switch (action) {
                case PUBLISH_EVENT:
                    state.publishEvent(machine);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    state.rejectEvent(machine);
                    break;
                default:
                    throw new IllegalArgumentException("'state action' " + action + " not available for admin");
            }
            event.setState(machine.getEventState());
        }

        EventFullResponse response = eventMapper.responseFromEntity(event);
        response.setConfirmedRequest(requestService.getParticipantCountForEvent(eventId));
        return response;
    }

    @Transactional(readOnly = true)
    public List<EventFullResponse> getEventsWithFilters(
            List<Long> eventIds,
            List<Long> userIds,
            List<String> states,
            List<Long> categoryIds,
            Optional<LocalDateTime> rangeStart,
            Optional<LocalDateTime> rangeEnd,
            Integer from,
            Integer size
    ) {
        QEventEntity event = QEventEntity.eventEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!eventIds.isEmpty()) {
            booleanBuilder.and(event.eventId.in(eventIds));

        }
        if (!userIds.isEmpty()) {
            booleanBuilder.and(event.initiator.userId.in(userIds));
        }

        if (!states.isEmpty()) {
            booleanBuilder.and(event.state.in(states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList()))
            );
        }

        if (!categoryIds.isEmpty()) {
            booleanBuilder.and(event.category.catId.in(categoryIds));
        }

        rangeStart.ifPresent(localDateTime -> booleanBuilder.and(event.eventDate.before(localDateTime).not()));
        rangeEnd.ifPresent(localDateTime -> booleanBuilder.and(event.eventDate.after(localDateTime).not()));

        List<EventEntity> events = eventRepository
                .getEventsForAdmin(booleanBuilder, from, size);

        Map<Long, Long> requests = requestService.getCountRequestsByListEventIds(
                events.stream()
                        .map(EventEntity::getEventId)
                        .collect(Collectors.toList())
        );

        return events.stream()
                .map(eventMapper::responseFromEntity)
                .peek(it -> it.setConfirmedRequest(requests.getOrDefault(it.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventShortResponse> getPublishedEvents(
            Optional<String> text,
            List<Long> categoryIds,
            Optional<Boolean> isPaid,
            Boolean isOnlyAvailable,
            Optional<LocalDateTime> rangeStart,
            Optional<LocalDateTime> rangeEnd,
            String sort,
            Integer from,
            Integer size,
            HttpServletRequest req
    ) throws IOException, InterruptedException {
        QEventEntity event = QEventEntity.eventEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder(event.state.eq(EventState.PUBLISHED));

        text.ifPresent(string -> booleanBuilder.and(event.annotation.containsIgnoreCase(string)
                .or(event.description.containsIgnoreCase(string))));

        if (!categoryIds.isEmpty()) {
            booleanBuilder.and(event.category.catId.in(categoryIds));
        }

        isPaid.ifPresent(aBoolean -> booleanBuilder.and(event.isPaid.eq(aBoolean)));

        OrderSpecifier<?> orderBy;
        switch (sort) {
            case "VIEWS":
                orderBy = event.eventId.desc(); //todo переделать на просмотры
                break;
            case "EVENT_DATE":
                orderBy = event.eventDate.desc();
                break;
            default:
                throw new IllegalArgumentException("sort " + sort + "does not available.");
        }


        if (isOnlyAvailable) {
            booleanBuilder.and(event.participantLimit.eq(0)
                    .or(event.participantLimit.gt(JPAExpressions
                                    .select(QRequestEntity.requestEntity.countDistinct())
                                            .from(QRequestEntity.requestEntity)
                                            .where(QRequestEntity.requestEntity.state.eq(RequestState.CONFIRMED)
                                                            .and(QRequestEntity.requestEntity.event.eventId.eq(event.eventId))
                                            )
                            )
                    )
            );
        }

        booleanBuilder.and(getPredicateByEventDate(rangeStart, rangeEnd));

        List<EventShortEntity> events = eventRepository.getPublishedEvent(booleanBuilder, orderBy, from, size);


        log.info("Вызов сервиса статистики POST /hit with {}, {}, {}", req.getRequestURI(),
                req.getRemoteAddr(), LocalDateTime.now());
        try {
            HttpResponse<String> response = client.addHit(req.getRequestURI(), req.getRemoteAddr(), LocalDateTime.now());
            log.info("POST /hit response : {}", response);
        } catch (ConnectException e) {
            log.error("ConnectException - {}", e.getMessage(), e);
        }

        Map<Long, Long> requests = requestService.getCountRequestsByListEventIds(
                events.stream()
                        .map(EventShortEntity::getEventId)
                        .collect(Collectors.toList())
        );

        return events.stream()
                .map(eventMapper::shortResponseFromShortEntity)
                .peek(it -> it.setConfirmedRequest(requests.getOrDefault(it.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullResponse getPublishedEventById(Long eventId, HttpServletRequest req)
            throws IOException, InterruptedException {
        EventEntity event = checkEventIsExistAndGet(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NoSuchElementException("Event with id=" + eventId + " was not found");
        }

        log.info("Вызов сервиса статистики POST /hit with {}, {}, {}", req.getRequestURI(),
                req.getRemoteAddr(), LocalDateTime.now());
        try {
            HttpResponse<String> response = client.addHit(req.getRequestURI(), req.getRemoteAddr(), LocalDateTime.now());
            log.info("POST /hit response : {}", response);
        } catch (ConnectException e) {
            log.error("ConnectException - {}", e.getMessage(), e);
        }

        EventFullResponse response = eventMapper.responseFromEntity(event);
        response.setConfirmedRequest(requestService.getParticipantCountForEvent(eventId));
        return response;
    }

    public EventEntity checkEventIsExistAndGet(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " was not found"));
    }

    public List<EventEntity> checkListEventsIsExistAndGet(Collection<Long> eventIds) {
        return eventRepository.getListEvents(eventIds);
    }

    public void checkUserIsEventInitiator(Long userId, EventEntity event) {
        if (!event.getInitiator().getUserId().equals(userId)) {
            throw new NoSuchElementException("Event with id=" + event.getEventId() + " was not found");
        }
    }

    private void checkThatEventIsAvailableForUpdate(EventEntity entity) {
        if (entity.getState() != EventState.PENDING && entity.getState() != EventState.CANCELED) {
            throw new IllegalStateEventException();
        }
    }

    private void checkEventDateStartTime(LocalDateTime eventDate, Long lag) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(lag))) {
            throw new StartTimeEventException(eventDate.toString());
        }
    }

    private BooleanExpression getPredicateByEventDate(Optional<LocalDateTime> rangeStart,
                                                      Optional<LocalDateTime> rangeEnd) {

        if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            return QEventEntity.eventEntity.eventDate.between(rangeStart.get(), rangeEnd.get());

        } else if (rangeStart.isEmpty() && rangeEnd.isEmpty()) {
            return QEventEntity.eventEntity.eventDate.after(LocalDateTime.now());
        }

        if (rangeStart.isPresent()) {
            return QEventEntity.eventEntity.eventDate.before(rangeStart.get()).not();
        } else {
            return QEventEntity.eventEntity.eventDate.after(rangeEnd.get()).not();
        }
    }
}
