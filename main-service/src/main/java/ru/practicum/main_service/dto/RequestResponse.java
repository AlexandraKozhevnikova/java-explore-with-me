package ru.practicum.main_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.main_service.model.RequestState;

import java.time.LocalDateTime;

public class RequestResponse {

    @JsonProperty("id")
    private Long requestId;
    @JsonProperty("requester")
    private Long participantId;
    @JsonProperty("event")
    private Long eventId;
    @JsonProperty("status")
    private RequestState state;
    @JsonProperty("created")
    private LocalDateTime createdOn;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
