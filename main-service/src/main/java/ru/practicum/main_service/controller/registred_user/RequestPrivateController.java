package ru.practicum.main_service.controller.registred_user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.RequestResponse;
import ru.practicum.main_service.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService service;

    public RequestPrivateController(RequestService service) {
        this.service = service;
    }

    @PostMapping
    RequestResponse createParticipationRequest(@PathVariable Long userId,
                                               @RequestParam Long eventId) {
        return service.createParticipationRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestResponse> getUserRequest(@PathVariable Long userId){
        return service.getUserRequests(userId);
    }
}
