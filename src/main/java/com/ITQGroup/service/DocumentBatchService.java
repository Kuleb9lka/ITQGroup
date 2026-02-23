package com.ITQGroup.service;

import com.ITQGroup.dto.document.DocumentBatchCreateRequestDto;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentShortResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DocumentBatchService {

    Page<DocumentResponseDto> getByListIds(List<Long> ids, DocumentPageableDto pageDto);

    List<DocumentShortResponseDto> batchCreate(DocumentBatchCreateRequestDto dto);

    List<DocumentProcessingResultDto> sendBatchSubmitted(Long authorId, List<Long> ids);

    List<DocumentProcessingResultDto> sendBatchApproved(Long authorId, List<Long> ids);
}
