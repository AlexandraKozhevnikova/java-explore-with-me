package ru.practicum.mainservice.dto;

import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.mainservice.model.EventStatus;

import java.time.LocalDateTime;

public class FullEventResponse {
    private String title;
    private String annotation;
    private CategoryResponse category;
    //private Integer confirmedRequest; todo
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private Integer participantLimit;
    private Boolean isPaid;
    private Boolean isModerationRequired;
    private LocalDateTime publishedOn; //format
    private EventStatus state;
    //private Long views; todo
}
