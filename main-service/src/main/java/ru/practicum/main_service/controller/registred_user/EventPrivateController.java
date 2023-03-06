package ru.practicum.main_service.controller.registred_user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.event.EventFullResponse;
import ru.practicum.main_service.dto.event.EventShortResponse;
import ru.practicum.main_service.dto.event.NewEventRequest;
import ru.practicum.main_service.dto.event.UpdateEventRequest;
import ru.practicum.main_service.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    public EventPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullResponse createEvent(@RequestBody @Valid NewEventRequest request,
                                         @PathVariable Long userId) {
        return eventService.createEvent(userId, request);
    }

    @GetMapping
    public List<EventShortResponse> getUserEvents(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullResponse getUserEventById(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullResponse updateUserEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @RequestBody UpdateEventRequest request) {
        return eventService.updateUserEvent(userId, eventId, request);
    }

}
