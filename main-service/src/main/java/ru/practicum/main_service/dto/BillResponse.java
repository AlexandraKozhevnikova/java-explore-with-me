package ru.practicum.main_service.dto;

import ru.practicum.main_service.dto.event.EventForBillResponse;
import ru.practicum.main_service.model.BillState;

import java.time.LocalDateTime;

public class BillResponse {
    private Long id;
    private UserResponse participant;
    private EventForBillResponse event;
    private AmountDto amount;
    private BillState state;
    private LocalDateTime createdOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserResponse getParticipant() {
        return participant;
    }

    public void setParticipant(UserResponse participant) {
        this.participant = participant;
    }

    public EventForBillResponse getEvent() {
        return event;
    }

    public void setEvent(EventForBillResponse event) {
        this.event = event;
    }

    public AmountDto getAmount() {
        return amount;
    }

    public void setAmount(AmountDto amount) {
        this.amount = amount;
    }

    public BillState getState() {
        return state;
    }

    public void setState(BillState state) {
        this.state = state;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
