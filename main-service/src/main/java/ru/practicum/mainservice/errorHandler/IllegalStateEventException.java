package ru.practicum.mainservice.errorHandler;

public class IllegalStateEventException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Event must not be published";

    public IllegalStateEventException() {
        this(DEFAULT_MESSAGE);
    }

    public IllegalStateEventException(String message) {
        super(message);
    }
}
