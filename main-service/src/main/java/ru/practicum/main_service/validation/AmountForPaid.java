package ru.practicum.main_service.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Constraint(validatedBy = AmountPaidValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AmountForPaid {
    String message() default "'amount' is possible only for paid events";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
