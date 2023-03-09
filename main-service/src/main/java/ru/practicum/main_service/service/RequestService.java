package ru.practicum.main_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.RequestResponse;
import ru.practicum.main_service.mapper.RequestMapper;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.QRequestEntity;
import ru.practicum.main_service.model.RequestEntity;
import ru.practicum.main_service.model.RequestState;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.model.eventStateMachine.EventState;
import ru.practicum.main_service.repository.RequestRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    public RequestService(RequestMapper requestMapper, RequestRepository requestRepository, UserService userService,
                          EventService eventService) {
        this.requestMapper = requestMapper;
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Transactional
    public RequestResponse createParticipationRequest(Long userId, Long eventId) {
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        EventEntity event = eventService.checkEventIsExistAndGet(eventId);

        checkThatInitiatorIsNotParticipant(user, event);
        checkEventIsAvailableForAddParticipant(event);

        RequestEntity request = new RequestEntity();
        request.setParticipant(user);
        request.setEvent(event);
        request.setState(RequestState.PENDING);

        request = requestRepository.save(request);

        if (!event.getModerationRequired() || event.getParticipantLimit() == 0) {
            request = autoConfirm(request.getRequestId());
        }

        return requestMapper.responseFromEntity(request);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RequestEntity autoConfirm(Long requestId) {
        RequestEntity request = checkRequestIsExistAndGet(requestId);

        checkEventIsAvailableForAddParticipant(request.getEvent());
        request.setState(RequestState.CONFIRMED);
        return request;
    }

    @Transactional(readOnly = true)
    public List<RequestResponse> getUserRequests(Long userId) {
        userService.checkUserIsExistAndGetById(userId);
        return requestRepository.findAll(QRequestEntity.requestEntity.participant.userId.eq(userId))
                .stream()
                .map(requestMapper::responseFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestResponse cancelUserRequest(Long userId, Long requestId) {
        userService.checkUserIsExistAndGetById(userId);
        RequestEntity request = checkRequestIsExistAndGet(requestId);
        if (!request.getParticipant().getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not participant.");
        }
        if (request.getState() != RequestState.REJECTED) {
            request.setState(RequestState.REJECTED);
        }
        return requestMapper.responseFromEntity(request);
    }

    public RequestEntity checkRequestIsExistAndGet(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("request with id=" + requestId + " does not exist."));
    }

    public List<RequestResponse> getRequestsForEvent(Long userId, Long eventId) {
        userService.checkUserIsExistAndGetById(userId);
        EventEntity event = eventService.checkEventIsExistAndGet(eventId);
        eventService.checkUserIsInitiatorEvent(userId, event);

        return requestRepository.findAll(QRequestEntity.requestEntity.event.eventId.eq(eventId))
                .stream()
                .map(requestMapper::responseFromEntity)
                .collect(Collectors.toList());
    }

    private void checkEventIsAvailableForAddParticipant(EventEntity event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalArgumentException("Event is not PUBLISHED");
        }

        if (event.getParticipantLimit() != 0
                && event.getParticipantLimit() <= getParticipantCountForEvent(event.getEventId())) {
            throw new IllegalArgumentException("Event has not free places.");
        }
    }

    private long getParticipantCountForEvent(Long eventId) {
        return requestRepository.count(QRequestEntity.requestEntity.event.eventId.eq(eventId)
                .and(QRequestEntity.requestEntity.state.eq(RequestState.CONFIRMED)));
    }

    private void checkThatInitiatorIsNotParticipant(UserEntity user, EventEntity event) {
        if (event.getInitiator().equals(user)) {
            throw new IllegalArgumentException("Initiator can not be participant.");
        }
    }

}
