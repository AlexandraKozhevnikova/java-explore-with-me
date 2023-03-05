package ru.practicum.mainservice.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.event.EventFullResponse;
import ru.practicum.mainservice.dto.event.UpdateEventRequest;
import ru.practicum.mainservice.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("admin/events")
public class EventAdminController {
    private final EventService eventService;

    public EventAdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullResponse> getEventsForAdmin(
            @RequestParam(name = "users", required = false) Optional<List<Long>> userIds,
            @RequestParam(name = "states", required = false) Optional<List<String>> states,
            @RequestParam(name = "categories", required = false) Optional<List<Long>> categoryIds,
            @RequestParam(name = "rangeStart", required = false) Optional<LocalDateTime> rangeStart,
            @RequestParam(name = "rangeEnd", required = false) Optional<LocalDateTime> rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return eventService.getEventsForAdmin(userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullResponse updateEventsForAdmin(@PathVariable Long eventId,
                                                  @RequestBody UpdateEventRequest request) {
        return eventService.updateUserEventForAdmin(eventId, request);
    }
}
