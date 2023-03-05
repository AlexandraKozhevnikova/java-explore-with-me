package ru.practicum.mainservice.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventStartTimeValidation implements ConstraintValidator<StartTime, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(LocalDateTime.now().plusHours(2L));
    }
}
