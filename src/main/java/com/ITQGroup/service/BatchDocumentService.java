package com.ITQGroup.service;

import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BatchDocumentService {

    Page<DocumentResponseDto> getByListIds(List<Long> ids, DocumentPageableDto pageDto);

    List<DocumentProcessingResultDto> sendBatchSubmitted(Long authorId, List<Long> ids);

    List<DocumentProcessingResultDto> sendBatchApproved(Long authorId, List<Long> ids);


}
