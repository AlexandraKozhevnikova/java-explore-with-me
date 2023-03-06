package ru.practicum.main_service.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.model.eventStateMachine.EventAction;
import ru.practicum.main_service.model.eventStateMachine.EventState;
import ru.practicum.main_service.model.eventStateMachine.StateMachine;
import ru.practicum.main_service.repository.EventRepository;
import ru.practicum.statisticclient.StatisticClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatisticClient client;
    private static final Logger log = LogManager.getLogger(EventService.class);


    public EventService(EventMapper eventMapper, EventRepository eventRepository, UserService userService,
                        CategoryService categoryService
            ,
                        StatisticClient client
                        ) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.categoryService = categoryService;
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

        return events.stream()
                .map(eventMapper::shortResponseFromShortEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullResponse getUserEventById(Long userId, Long eventId) {
        userService.checkUserIsExistAndGetById(userId);
        EventEntity entity = checkEventIsExistAndGet(eventId);
        checkIsInitiatorEvent(userId, entity);
        return eventMapper.responseFromEntity(entity);
    }

    @Transactional
    public EventFullResponse updateUserEvent(Long userId, Long eventId, UpdateEventRequest request) {
        userService.checkUserIsExistAndGetById(userId);
        EventEntity event = checkEventIsExistAndGet(eventId);
        checkEventDateStartTime(event.getEventDate(), 2L);
        checkIsInitiatorEvent(userId, event);
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

        return eventMapper.responseFromEntity(event);
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

        return eventMapper.responseFromEntity(event);

    }

    @Transactional
    public List<EventFullResponse> getEventsForAdmin(
            Optional<List<Long>> userIds,
            Optional<List<String>> states,
            Optional<List<Long>> categoryIds,
            Optional<LocalDateTime> rangeStart,
            Optional<LocalDateTime> rangeEnd,
            Integer from,
            Integer size
    ) {

        QEventEntity event = QEventEntity.eventEntity;

        BooleanExpression byUserIds = Expressions.TRUE.isTrue();
        if (userIds.isPresent()) {
            List<UserEntity> users = userService.checkListUsersIsExist(userIds.get());
            if (!users.isEmpty()) {
                byUserIds = event.initiator.in(users);
            }
        }

        BooleanExpression byStates = Expressions.TRUE.isTrue();
        if (states.isPresent() && !states.get().isEmpty()) {
            byStates = event.state.in(states.get().stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList()));
        }

        BooleanExpression byCategoryIds = getPredicateByCategoriesId(categoryIds);

        BooleanExpression byRangeStart = Expressions.TRUE.isTrue();
        if (rangeStart.isPresent()) {
            byRangeStart = event.eventDate.before(rangeStart.get()).not();
        }

        BooleanExpression byRangeEnd = Expressions.TRUE.isTrue();
        if (rangeEnd.isPresent()) {
            byRangeEnd = event.eventDate.after(rangeEnd.get()).not();
        }

        List<EventEntity> entities = eventRepository
                .getEventsForAdmin(byUserIds, byStates, byCategoryIds, byRangeStart, byRangeEnd, from, size);

        return entities.stream()
                .map(eventMapper::responseFromEntity)
                .collect(Collectors.toList());
    }

    public List<EventShortResponse> getPublishedEvents(
            Optional<String> text,
            Optional<List<Long>> categoryIds,
            Optional<Boolean> isPaid,
            Optional<Boolean> isOnlyAvailable,
            Optional<LocalDateTime> rangeStart,
            Optional<LocalDateTime> rangeEnd,
            Optional<String> sort,
            Integer from,
            Integer size
    ) throws IOException, InterruptedException {
        QEventEntity event = QEventEntity.eventEntity;

        BooleanExpression byText = Expressions.TRUE.isTrue();
        if (text.isPresent()) {
            byText = event.annotation.containsIgnoreCase(text.get())
                    .or(event.description.containsIgnoreCase(text.get()));
        }

        BooleanExpression byCategoryIds = getPredicateByCategoriesId(categoryIds);

        BooleanExpression byIsPaid = Expressions.TRUE.isTrue();
        if (isPaid.isPresent()) {
            byIsPaid = event.isPaid.eq(isPaid.get());
        }

        OrderSpecifier<?> orderBy = QEventEntity.eventEntity.eventId.asc();
        if (sort.isPresent()) {
            switch (sort.get()) {
                case "VIEWS":
                    orderBy = QEventEntity.eventEntity.eventId.desc(); //todo
                    break;
                case "EVENT_DATE":
                    orderBy = QEventEntity.eventEntity.eventDate.desc();
                    break;
                default:
                    throw new IllegalArgumentException("sort " + sort + "does not available.");

            }
        }

        BooleanExpression byIsOnlyAvailable = Expressions.TRUE.isTrue();
        if (isOnlyAvailable.isPresent()) {
            //todo есть ли модерация, лимит  и количество апрувов
        }

        BooleanExpression byEventDate = getPredicateByEventDate(rangeStart, rangeEnd);

        List<EventShortEntity> events = eventRepository.getPublishedEvent(
                byText,
                byCategoryIds,
                byIsPaid,
                byIsOnlyAvailable,
                byEventDate,
                orderBy,
                from,
                size);

        HttpResponse<String> response = client.addHit("/events",
                "из метода", LocalDateTime.now()); //todo подумать над аспектами и может в контроллер
        log.info("Вызов сервиса статистики POST /hit  , response : " + response);

        return events.stream()
                .map(eventMapper::shortResponseFromShortEntity)
                .collect(Collectors.toList());
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

    private BooleanExpression getPredicateByCategoriesId(Optional<List<Long>> categoryIds) {
        BooleanExpression byCategoryIds = Expressions.TRUE.isTrue();
        if (categoryIds.isPresent()) {
            List<CategoryEntity> categories = categoryService.checkListCategoryIsExist(categoryIds.get());
            if (!categories.isEmpty()) {
                byCategoryIds = QEventEntity.eventEntity.category.in(categories);
            }
        }
        return byCategoryIds;
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
