package com.ITQGroup.dto.document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentPageableDto {

    @Min(value = 0, message = "Page number can't be negative")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be 1 at least")
    @Max(value = 50, message = "Max page size 50")
    private Integer size = 10;

    @Pattern(regexp = "id|uniqueNumber|authorId|name|status|createDate|updateDate", message = "Sort field not matches any document field")
    private String sortBy = "id";

    private Boolean asc = true;
}
