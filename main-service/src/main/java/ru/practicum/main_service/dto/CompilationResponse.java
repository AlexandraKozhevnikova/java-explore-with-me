package ru.practicum.main_service.dto;

import ru.practicum.main_service.dto.event.EventShortResponse;

import java.util.List;

public class CompilationResponse {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortResponse> events;

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

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public List<EventShortResponse> getEvents() {
        return events;
    }

    public void setEvents(List<EventShortResponse> events) {
        this.events = events;
    }
}
