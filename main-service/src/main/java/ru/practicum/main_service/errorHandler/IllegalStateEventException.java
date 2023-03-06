package ru.practicum.main_service.errorHandler;

public class IllegalStateEventException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Only pending or canceled events can be changed";

    public IllegalStateEventException() {
        this(DEFAULT_MESSAGE);
    }

    public IllegalStateEventException(String message) {
        super(message);
    }
}
