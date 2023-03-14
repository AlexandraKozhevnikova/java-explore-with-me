package ru.practicum.main_service.controller.registred_user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.RequestBulkUpdateRequest;
import ru.practicum.main_service.dto.RequestBulkUpdateResponse;
import ru.practicum.main_service.dto.RequestResponse;
import ru.practicum.main_service.dto.event.EvenPaymentsReport;
import ru.practicum.main_service.dto.event.EventFullResponse;
import ru.practicum.main_service.dto.event.EventShortResponse;
import ru.practicum.main_service.dto.event.NewEventRequest;
import ru.practicum.main_service.dto.event.UpdateEventRequest;
import ru.practicum.main_service.service.BillService;
import ru.practicum.main_service.service.EventService;
import ru.practicum.main_service.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Tag(name = "Private: События", description = "Создавать, обновлять и получать созданные события")
@RestController
@Validated
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    private final RequestService requestService;
    private final BillService billService;

    public EventPrivateController(EventService eventService, RequestService requestService, BillService billService) {
        this.eventService = eventService;
        this.requestService = requestService;
        this.billService = billService;
    }

    @Operation(
            summary = "Создание события",
            description = "Добавлены параметры для стоимости участия в событии."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullResponse createEvent(@RequestBody @Valid NewEventRequest request,
                                         @PathVariable Long userId) {
        return eventService.createEvent(userId, request);
    }

    @GetMapping
    public List<EventShortResponse> getUserEvents(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullResponse getUserEventById(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @Operation(
            summary = "Обновление события",
            description = "Обновлять стоимость события нельзя"
    )
    public EventFullResponse updateUserEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @RequestBody @Valid UpdateEventRequest request) {
        return eventService.updateUserEvent(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestResponse> getRequestsForEvent(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        return requestService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestBulkUpdateResponse approveRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId,
                                                     @RequestBody @Valid RequestBulkUpdateRequest body) {
        return requestService.approveRequestOfEvent(userId, eventId, body);
    }

    @GetMapping("/payments")
    public List<EvenPaymentsReport> getEventsPayments(@PathVariable Long userId) {
        return billService.getEventsPayments(userId);
    }

}
