package ru.practicum.main_service.dto.event;

import ru.practicum.main_service.dto.CategoryResponse;

import java.time.LocalDateTime;

public class EventShortResponse {
    private Long id;
    private String title;
    private String annotation;
    private CategoryResponse category;
    private Long confirmedRequest;
    private LocalDateTime eventDate;
    private InitiatorDto initiator;
    private Boolean paid;

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    private Long views;


    public Long getConfirmedRequest() {
        return confirmedRequest;
    }

    public void setConfirmedRequest(Long confirmedRequest) {
        this.confirmedRequest = confirmedRequest;
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

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
