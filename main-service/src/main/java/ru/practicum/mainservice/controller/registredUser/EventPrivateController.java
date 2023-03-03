package ru.practicum.mainservice.controller.registredUser;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.event.FullEventResponse;
import ru.practicum.mainservice.dto.event.NewEventRequest;
import ru.practicum.mainservice.service.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping("users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    public EventPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public FullEventResponse createEvent(@RequestBody @Valid NewEventRequest request,
                                         @PathVariable Long userId) {
        return eventService.createEvent(userId, request);
    }
}
