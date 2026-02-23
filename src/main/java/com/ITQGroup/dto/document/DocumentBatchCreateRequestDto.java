package com.ITQGroup.dto.document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentBatchCreateRequestDto {

    @NotNull(message = "Author ID can't be null")
    @Positive(message = "Author ID can't be negative or zero")
    private Long authorId;

    @NotNull(message = "Quantity can't be null")
    @Positive(message = "Quantity can't be negative or zero")
    private Integer documentQuantityToCreate;
}
