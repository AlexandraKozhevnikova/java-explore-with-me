package ru.practicum.mainservice.validation;

import ru.practicum.mainservice.errorHandler.StartTimeEventException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventStartTimeValidation implements ConstraintValidator<StartTime, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value != null && value.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new StartTimeEventException(value.toString());
        }
        return value != null && value.isAfter(LocalDateTime.now().plusHours(2L));
    }
}
