package com.ITQGroup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentProcessingResultDto {

    private Long documentId;

    private String status;

    private String responseMessage;
}
