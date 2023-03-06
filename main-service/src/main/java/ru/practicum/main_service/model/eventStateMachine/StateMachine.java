package ru.practicum.main_service.model.eventStateMachine;

public class StateMachine {

    private EventState eventState;

    public StateMachine(EventState eventState) {
        this.eventState = eventState;
    }

    public EventState getEventState() {
        return eventState;
    }

    public void setEventState(EventState eventState) {
        this.eventState = eventState;
    }
}
