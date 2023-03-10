package ru.practicum.main_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import statisticcommon.HitResponse;

import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
    private final StatisticClient statisticClient;
    private final ObjectMapper mapper;
    private static final Logger log = LogManager.getLogger(EventService.class);


    public EventService(EventMapper eventMapper, EventRepository eventRepository, UserService userService,
                        CategoryService categoryService, @Lazy RequestService requestService, StatisticClient client,
                        ObjectMapper mapper) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.requestService = requestService;
        this.statisticClient = client;
        this.mapper = mapper;
    }

    @Transactional
    public EventFullResponse createEvent(Long userId, NewEventRequest request) {
        //проверки
        checkEventDateStartTime(request.getEventDate(), 2L);
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        CategoryEntity category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        //логика
        EventEntity newEventEntity = eventMapper.entityFromNewRequest(request, user, category);
        StateMachine machine = new StateMachine(EventState.CREATED);
        machine.getEventState().sentToReview(machine);
        newEventEntity.setState(machine.getEventState());
        //подготовка ответа
        EventEntity event = eventRepository.save(newEventEntity);
        return eventMapper.responseFromEntity(event);
    }

    @Transactional(readOnly = true)
    public List<EventShortResponse> getUserEvents(Long userId, Integer from, Integer size) {
        //проверки
        userService.checkUserIsExistAndGetById(userId);
        //логика
        List<EventShortEntity> events = eventRepository.getUserEvents(userId, from, size);
        //подготовка ответа
        Map<Long, Long> requests = requestService.getCountRequestsByListEvents(events);
        Map<String, Long> views = getHits(events);

        return events.stream()
                .map(eventMapper::shortResponseFromShortEntity)
                .peek(it -> it.setConfirmedRequests(requests.getOrDefault(it.getId(), 0L)))
                .peek(it -> it.setViews(views.getOrDefault("/events/" + it.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullResponse getUserEventById(Long userId, Long eventId) {
        // проверки и логика
        userService.checkUserIsExistAndGetById(userId);
        EventEntity event = checkEventIsExistAndGet(eventId);
        checkUserIsEventInitiator(userId, event);
        EventFullResponse response = eventMapper.responseFromEntity(event);
        //подготовка ответа
        response.setConfirmedRequests(requestService.getParticipantCountForEvent(eventId));
        response.setViews(getHits(List.of(event)).getOrDefault("/events/" + event.getEventId(), 0L));
        return response;
    }

    @Transactional
    public EventFullResponse updateUserEvent(Long userId, Long eventId, UpdateEventRequest request) {
        //проверки
        userService.checkUserIsExistAndGetById(userId);
        EventEntity event = checkEventIsExistAndGet(eventId);
        checkEventDateStartTime(event.getEventDate(), 2L);
        checkUserIsEventInitiator(userId, event);
        checkThatEventIsAvailableForUpdate(event);
        CategoryEntity category = null;
        if (request.getCategory() != null) {
            category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        }
        //логика
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
        //подготовка ответа
        EventFullResponse response = eventMapper.responseFromEntity(event);
        response.setConfirmedRequests(requestService.getParticipantCountForEvent(eventId));
        response.setViews(getHits(List.of(event)).getOrDefault("/events/" + event.getEventId(), 0L));
        return response;
    }

    @Transactional
    public EventFullResponse updateUserEventForAdmin(Long eventId, UpdateEventRequest request) {
        //проверки
        EventEntity event = checkEventIsExistAndGet(eventId);
        checkEventDateStartTime(event.getEventDate(), 1L);
        checkThatEventIsAvailableForUpdate(event);
        CategoryEntity category = null;
        if (request.getCategory() != null) {
            category = categoryService.checkCategoryIsExistAndGet(request.getCategory());
        }
        //логика
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
        //подготовка ответа
        EventFullResponse response = eventMapper.responseFromEntity(event);
        response.setConfirmedRequests(requestService.getParticipantCountForEvent(eventId));
        response.setViews(getHits(List.of(event)).getOrDefault("/events/" + event.getEventId(), 0L));
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
        //подготовка ответа
        Map<Long, Long> requests = requestService.getCountRequestsByListEvents(events);
        Map<String, Long> views = getHits(events);
        return events.stream()
                .map(eventMapper::responseFromEntity)
                .peek(it -> it.setConfirmedRequests(requests.getOrDefault(it.getId(), 0L)))
                .peek(it -> it.setViews(views.getOrDefault("/events/" + it.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventShortResponse> getPublishedEvents(
            Optional<String> text, List<Long> categoryIds, Optional<Boolean> isPaid, Boolean isOnlyAvailable,
            Optional<LocalDateTime> rangeStart, Optional<LocalDateTime> rangeEnd, String sort,
            Integer from, Integer size, HttpServletRequest req
    ) {
        QEventEntity event = QEventEntity.eventEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder(event.state.eq(EventState.PUBLISHED));

        text.ifPresent(string -> booleanBuilder.and(event.annotation.containsIgnoreCase(string)
                .or(event.description.containsIgnoreCase(string))));

        if (!categoryIds.isEmpty()) {
            booleanBuilder.and(event.category.catId.in(categoryIds));
        }

        isPaid.ifPresent(aBoolean -> booleanBuilder.and(event.isPaid.eq(aBoolean)));

        OrderSpecifier<?> orderBy = event.eventDate.desc();

        Boolean hasNeedSortByViews = false;
        switch (sort) {
            case "VIEWS":
                hasNeedSortByViews = true;
                break;
            case "EVENT_DATE":
                orderBy = event.eventDate.desc();
                break;
            default:
                throw new IllegalArgumentException("sort " + sort + " does not available.");
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
        //отправка статистики
        sendHits(req);
        //подготовка ответа
        Map<Long, Long> requests = requestService.getCountRequestsByListEvents(events);
        Map<String, Long> views = getHits(events);
        Boolean finalHasNeedSortByViews = hasNeedSortByViews;
        return events.stream()
                .map(eventMapper::shortResponseFromShortEntity)
                .peek(it -> it.setConfirmedRequests(requests.getOrDefault(it.getId(), 0L)))
                .peek(it -> it.setViews(views.getOrDefault("/events/" + it.getId(), 0L)))
                .sorted((k1, k2) -> {
                            if (finalHasNeedSortByViews) {
                                return Long.compare(k2.getViews(), k1.getViews());
                            } else {
                                return 0;
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullResponse getPublishedEventById(Long eventId, HttpServletRequest req) {
        //проверки
        EventEntity event = checkEventIsExistAndGet(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NoSuchElementException("Event with id=" + eventId + " was not found");
        }
        //логика
        EventFullResponse response = eventMapper.responseFromEntity(event);
        response.setConfirmedRequests(requestService.getParticipantCountForEvent(eventId));
        response.setViews(getHits(List.of(event)).getOrDefault("/events/" + event.getEventId(), 0L));
        //отправка статистики
        sendHits(req);
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
            log.warn("user id = {} is not initiator event id = {}", userId, event.getInitiator().getUserId() );
            throw new NoSuchElementException("Event with id=" + event.getEventId() + " was not found");
        }
    }

    private Map<String, Long> getHits(Collection<? extends EventEntity> events) {
        Map<String, Long> hits = new HashMap<>();
        log.info("Вызов сервиса статистики GET /stats");
        try {
            HttpResponse<String> statResponse = statisticClient.getStatistics(
                    LocalDateTime.now().minusYears(10L),
                    LocalDateTime.now().plusDays(1L),
                    events.stream()
                            .map(EventEntity::getEventId)
                            .map(it -> "/events/" + it)
                            .collect(Collectors.toList()),
                    true
            );
            log.info("GET /stats response : {}", statResponse);

            HitResponse[] hitResponse = mapper.readValue(statResponse.body(), HitResponse[].class);
            hits = Arrays.stream(hitResponse)
                    .collect(Collectors.toMap(HitResponse::getUri, HitResponse::getHits));
        } catch (Exception e) {
            log.error("Exception - {}", e.getMessage(), e);
        }
        return hits;
    }

    private void sendHits(HttpServletRequest req) {
        //отправка статистики
        log.info("Вызов сервиса статистики POST /hit with {}, {}, {}", req.getRequestURI(),
                req.getRemoteAddr(), LocalDateTime.now());
        try {
            HttpResponse<String> response = statisticClient.addHit(req.getRequestURI(), req.getRemoteAddr(), LocalDateTime.now());
            log.info("POST /hit response : {}", response);
        } catch (ConnectException e) {
            log.error("ConnectException - {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception - {}", e.getMessage(), e);
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
