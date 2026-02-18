package com.ITQGroup.annotation;

import com.ITQGroup.validator.GenericDateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenericDateRangeValidator.class)
public @interface ValidDateRange {
    String message() default "Start date can't be after end date.";

    String startField();

    String endField();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
