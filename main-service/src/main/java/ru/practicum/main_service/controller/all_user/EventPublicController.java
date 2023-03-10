package ru.practicum.main_service.controller.all_user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.event.EventFullResponse;
import ru.practicum.main_service.dto.event.EventShortResponse;
import ru.practicum.main_service.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
@Validated
public class EventPublicController {

    private final EventService eventService;

    public EventPublicController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortResponse> getPublishedEvents(
            @RequestParam Optional<String> text,
            @RequestParam(name = "categories", defaultValue = "") List<Long> categoryIds,
            @RequestParam(name = "paid") Optional<Boolean> isPaid,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean isOnlyAvailable,
            @RequestParam(name = "rangeStart") Optional<LocalDateTime> rangeStart,
            @RequestParam(name = "rangeEnd") Optional<LocalDateTime> rangeEnd,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest req) {
        return eventService.getPublishedEvents(text, categoryIds, isPaid, isOnlyAvailable, rangeStart, rangeEnd,
                sort, from, size, req);
    }

    @GetMapping("/{id}")
    public EventFullResponse getPublishedEventById(@PathVariable(name = "id") Long eventId, HttpServletRequest req) {
        return eventService.getPublishedEventById(eventId, req);
    }
}
