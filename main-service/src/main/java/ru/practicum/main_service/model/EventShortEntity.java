package ru.practicum.main_service.model;

import java.time.LocalDateTime;

public class EventShortEntity extends EventEntity {
    private Long eventId;
    private String title;
    private CategoryEntity category;
    private String annotation;
    private LocalDateTime eventDate;
    private Boolean isPaid;
    private UserEntity initiator;

    public EventShortEntity() {
    }

    public EventShortEntity(Long eventId, String title, CategoryEntity category, String annotation,
                            LocalDateTime eventDate, Boolean isPaid, UserEntity initiator) {
        this.eventId = eventId;
        this.title = title;
        this.category = category;
        this.annotation = annotation;
        this.eventDate = eventDate;
        this.isPaid = isPaid;
        this.initiator = initiator;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
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

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public UserEntity getInitiator() {
        return initiator;
    }

    public void setInitiator(UserEntity initiator) {
        this.initiator = initiator;
    }
}
