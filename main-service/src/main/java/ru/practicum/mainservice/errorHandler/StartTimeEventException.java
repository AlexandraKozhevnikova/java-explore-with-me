package ru.practicum.mainservice.errorHandler;

public class StartTimeEventException extends RuntimeException {
    private final String value;

    public StartTimeEventException(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
