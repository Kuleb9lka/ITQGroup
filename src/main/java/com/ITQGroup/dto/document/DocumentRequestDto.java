package com.ITQGroup.dto.document;

import com.ITQGroup.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequestDto {

    @NotNull(message = "User ID can't be null")
    @Positive(message = "User ID can't be negative")
    private Long userId;

    @NotBlank(message = "Name can't be null or blank")
    @Size(min = 5, message = "Minimal document name length 5 symbols")
    private String name;

    @NotNull(message = "Status can't be null")
    private DocumentStatus status;
}
