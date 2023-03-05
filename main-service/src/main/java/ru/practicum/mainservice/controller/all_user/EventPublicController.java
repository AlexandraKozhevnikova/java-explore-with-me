package ru.practicum.mainservice.controller.all_user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.event.EventShortResponse;
import ru.practicum.mainservice.service.EventService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
public class EventPublicController {

    private final EventService eventService;

    public EventPublicController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortResponse> getEvents(
            @RequestParam Optional<String> text,
            @RequestParam(name = "categories") Optional<List<Long>> categoryIds,
            @RequestParam(name = "paid") Optional<Boolean> isPaid,
            @RequestParam(name = "onlyAvailable") Optional<Boolean> isOnlyAvailable,
            @RequestParam(name = "rangeStart") Optional<LocalDateTime> rangeStart,
            @RequestParam(name = "rangeEnd") Optional<LocalDateTime> rangeEnd,
            @RequestParam Optional<String> sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) throws IOException, InterruptedException {
        return eventService.getPublishedEvents(text, categoryIds, isPaid, isOnlyAvailable, rangeStart, rangeEnd,
                sort, from, size);
    }

//    @GetMapping("/{eventId}")
//    public EventFullResponse getEventById(@PathVariable Long userId,
//                                          @PathVariable Long eventId) {
//        return eventService.getEventById(userId, eventId);
//    }
}
