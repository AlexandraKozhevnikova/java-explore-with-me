package ru.practicum.main_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.RequestResponse;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.RequestEntity;
import ru.practicum.main_service.model.RequestState;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.repository.RequestRepository;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    public RequestService(RequestRepository requestRepository, UserService userService, EventService eventService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Transactional
    public RequestResponse createParticipationRequest(Long userId, Long eventId) {
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        EventEntity event = eventService.checkEventIsExistAndGet(eventId);
        checkThanInitiatorIsNotParticipant(user, event);

        RequestEntity request = new RequestEntity();
        request.setParticipant(user);
        request.setEvent(event);
        request.setState(RequestState.PENDING); //if event moderation


    }

    private void checkThanInitiatorIsNotParticipant(UserEntity user, EventEntity event) {
        if (event.getInitiator().equals(user)) {
            throw new IllegalArgumentException("Initiator can not be participant.");
        }
    }


}
