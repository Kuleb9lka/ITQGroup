package com.ITQGroup.dto.filter;

import com.ITQGroup.annotation.ValidDateRange;
import com.ITQGroup.enums.DocumentStatus;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ValidDateRange
public class DocumentFilterDto {

    private DocumentStatus status;

    @Positive(message = "Author ID can't be negative")
    private Long authorId;

    private LocalDateTime createDateFrom;
    private LocalDateTime createDateTo;

    private LocalDateTime updateDateFrom;
    private LocalDateTime updateDateTo;
}
