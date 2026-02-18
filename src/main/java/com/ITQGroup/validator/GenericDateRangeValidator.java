package com.ITQGroup.validator;

import com.ITQGroup.annotation.ValidDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class GenericDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field start = value.getClass().getDeclaredField(startField);
            Field end = value.getClass().getDeclaredField(endField);
            start.setAccessible(true);
            end.setAccessible(true);

            Object startValue = start.get(value);
            Object endValue = end.get(value);

            if (startValue == null || endValue == null) return true;

            if (startValue instanceof Comparable && endValue instanceof Comparable) {
                return ((Comparable) startValue).compareTo(endValue) <= 0;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
