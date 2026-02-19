package com.ITQGroup.dto.approval_registry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRegistryRequestDto {

    @NotNull(message = "Document ID can't be null")
    @Positive(message = "Document ID can't be negative")
    private Long documentId;

    @NotNull(message = "Author ID can't be null")
    @Positive(message = "Author ID can't be negative")
    private Long authorId;

    @NotNull(message = "Approval date time cant be null")
    @PastOrPresent(message = "Approval date time cant be in future")
    private LocalDateTime approvedAt;
}
