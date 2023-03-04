package ru.practicum.mainservice.errorHandler;

import javax.validation.ValidationException;

public class StartTimeEventException extends ValidationException {
    private final String value;

    public StartTimeEventException(String value) {
        super("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                "Value: " + value);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
