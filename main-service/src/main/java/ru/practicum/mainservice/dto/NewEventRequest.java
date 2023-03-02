package ru.practicum.mainservice.dto;

import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.mainservice.validation.StartTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class NewEventRequest {
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotNull
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StartTime
    private LocalDateTime eventDate;
    @NotNull
    @Valid //todo нужно ли тут?
    private LocationDto location;
    private Integer participantLimit;
    private Boolean isPaid;
    private Boolean isModerationRequired;

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

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
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

    public Boolean getModerationRequired() {
        return isModerationRequired;
    }

    public void setModerationRequired(Boolean moderationRequired) {
        isModerationRequired = moderationRequired;
    }
}
