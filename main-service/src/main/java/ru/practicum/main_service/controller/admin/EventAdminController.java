package ru.practicum.main_service.controller.admin;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.event.EventFullResponse;
import ru.practicum.main_service.dto.event.UpdateEventRequest;
import ru.practicum.main_service.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Validated
@RestController
@RequestMapping("admin/events")
public class EventAdminController {
    private final EventService eventService;

    public EventAdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullResponse> getEventsForAdmin(
            @RequestParam(name = "users", defaultValue = "") List<Long> userIds,
            @RequestParam(name = "states", defaultValue = "") List<String> states,
            @RequestParam(name = "categories", defaultValue = "") List<Long> categoryIds,
            @RequestParam(name = "rangeStart", required = false) Optional<LocalDateTime> rangeStart,
            @RequestParam(name = "rangeEnd", required = false) Optional<LocalDateTime> rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return eventService.getEventsWithFilters(Collections.EMPTY_LIST, userIds, states, categoryIds, rangeStart,
                rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullResponse updateEventsForAdmin(@PathVariable Long eventId,
                                                  @RequestBody UpdateEventRequest request) {
        return eventService.updateUserEventForAdmin(eventId, request);
    }
}
