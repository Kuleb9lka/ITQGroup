package com.ITQGroup.dto.document;

import com.ITQGroup.enums.DocumentStatus;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateStatusDto {

    @Positive(message = "Author ID can't be negative")
    private Long authorId;

    private DocumentStatus oldStatus;

    private DocumentStatus newStatus;
}
