package ru.practicum.main_service.validation;

import ru.practicum.main_service.dto.event.NewEventRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Сумму можно указывать только для платных мероприятий
 */
public class AmountPaidValidator implements ConstraintValidator<AmountForPaid, NewEventRequest> {

    @Override
    public boolean isValid(NewEventRequest request, ConstraintValidatorContext context) {
        if (request.getAmount() != null) {
            return request.getPaid();
        }
        return true;
    }
}
