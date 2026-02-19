package com.ITQGroup.dto.document;

import com.ITQGroup.enums.DocumentStatus;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUpdateDto {

    @Positive(message = "User ID can't be negative")
    private Long userId;

    @Size(min = 5, message = "Minimal document name length 5 symbols")
    private String name;

    private DocumentStatus documentStatus;

    @PastOrPresent(message = "Update date can't be in future")
    private LocalDateTime updateDate;
}
