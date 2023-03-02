package ru.practicum.mainservice.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long eventId;
    @Column(length = 120, nullable = false)
    String title;
    @ManyToOne
    @JoinColumn(name = "cat_id", nullable = false)
    CategoryEntity category;
    @Column(nullable = false)
    EventStatus status;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    UserEntity initiator;
    @Column(length = 2000, nullable = false)
    String annotation;
    @Column(length = 7000, nullable = false)
    String description;
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;
    @Column(nullable = false)
    Double lat;
    @Column(nullable = false)
    Double lon;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "paid")
    Boolean isPaid;
    @Column(name = "request_moderation")
    Boolean isModerationRequired;

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

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public UserEntity getInitiator() {
        return initiator;
    }

    public void setInitiator(UserEntity initiator) {
        this.initiator = initiator;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
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

    public Boolean getModerationRequired() {
        return isModerationRequired;
    }

    public void setModerationRequired(Boolean moderationRequired) {
        isModerationRequired = moderationRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventEntity that = (EventEntity) o;

        return eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return eventId.hashCode();
    }
}
