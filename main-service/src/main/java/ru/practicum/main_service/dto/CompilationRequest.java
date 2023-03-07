package ru.practicum.main_service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompilationRequest {
    @NotBlank
    @Size(max = 120)
    private String title;
    private Boolean pinned = false;
    private Set<Long> events = new HashSet<>();

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

    public Set<Long> getEvents() {
        return events;
    }

    public void setEvents(Set<Long> events) {
        this.events = events;
    }
}
