package ru.practicum.mainservice.dto.event;

import ru.practicum.mainservice.dto.CategoryResponse;
import ru.practicum.mainservice.model.EventState;

import java.time.LocalDateTime;

public class FullEventResponse {
    private Long id;
    private String title;
    private String annotation;
    private CategoryResponse category;
    //private Integer confirmedRequest; todo
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private InitiatorDto initiator;
    private LocationDto location;
    private Integer participantLimit;
    private Boolean isPaid;
    private Boolean requestModeration;
    private LocalDateTime publishedOn;
    private EventState state;
    //private Long views; todo

    public InitiatorDto getInitiator() {
        return initiator;
    }

    public void setInitiator(InitiatorDto initiator) {
        this.initiator = initiator;
    }

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

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public Integer getParticipantLimit() {
        return participantLimit;
    }

    public void setParticipantLimit(Integer participantLimit) {
        this.participantLimit = participantLimit;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public Boolean getRequestModeration() {
        return requestModeration;
    }

    public void setRequestModeration(Boolean requestModeration) {
        this.requestModeration = requestModeration;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }
}
