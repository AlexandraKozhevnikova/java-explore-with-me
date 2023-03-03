package ru.practicum.mainservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull
@Target(ElementType.FIELD)
@Constraint(validatedBy = EventStartTimeValidation.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface StartTime {
    String message() default "должно содержать дату, которая еще не наступила.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
