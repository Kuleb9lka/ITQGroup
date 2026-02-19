package com.ITQGroup.dto.document;

import com.ITQGroup.enums.DocumentStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateDto {

    @Positive(message = "Author ID can't be negative")
    private Long authorId;

    @Size(min = 5, message = "Minimal document name length 5 symbols")
    private String name;

    private DocumentStatus status;
}
