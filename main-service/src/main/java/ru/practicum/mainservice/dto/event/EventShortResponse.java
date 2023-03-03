package ru.practicum.mainservice.dto.event;

import ru.practicum.mainservice.dto.CategoryResponse;

import java.time.LocalDateTime;

public class EventShortResponse {
    private Long id;
    private String title;
    private String annotation;
    private CategoryResponse category;
    //private Integer confirmedRequest; todo
    private LocalDateTime eventDate;
    private InitiatorDto initiator;
    private Boolean paid;
    //private Long views; todo


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
