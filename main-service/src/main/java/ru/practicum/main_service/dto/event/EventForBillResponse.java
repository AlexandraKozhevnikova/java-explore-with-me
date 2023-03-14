package ru.practicum.main_service.dto.event;

import ru.practicum.main_service.dto.AmountDto;

import java.time.LocalDateTime;

public class EventForBillResponse {

    private Long id;
    private String title;
    private String annotation;
    private LocalDateTime eventDate;
    private InitiatorDto initiator;
    private AmountDto amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public InitiatorDto getInitiator() {
        return initiator;
    }

    public void setInitiator(InitiatorDto initiator) {
        this.initiator = initiator;
    }

    public AmountDto getAmount() {
        return amount;
    }

    public void setAmount(AmountDto amount) {
        this.amount = amount;
    }
}
