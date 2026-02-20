package com.ITQGroup.validator;

import com.ITQGroup.annotation.ValidDateRange;
import com.ITQGroup.dto.filter.DocumentFilterDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator
        implements ConstraintValidator<ValidDateRange, DocumentFilterDto> {

    @Override
    public boolean isValid(DocumentFilterDto dto, ConstraintValidatorContext context) {

        if (dto == null) {
            return true;
        }

        boolean createValid = true;
        if (dto.getCreateDateFrom() != null && dto.getCreateDateTo() != null) {
            createValid = !dto.getCreateDateFrom().isAfter(dto.getCreateDateTo());
        }

        boolean updateValid = true;
        if (dto.getUpdateDateFrom() != null && dto.getUpdateDateTo() != null) {
            updateValid = !dto.getUpdateDateFrom().isAfter(dto.getUpdateDateTo());
        }

        return createValid && updateValid;
    }
}
