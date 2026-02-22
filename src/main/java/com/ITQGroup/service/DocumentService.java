package com.ITQGroup.service;

import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateStatusDto;
import com.ITQGroup.dto.filter.DocumentFilterDto;
import com.ITQGroup.enums.DocumentStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DocumentService {

    DocumentResponseDto getById(Long id);

    List<DocumentResponseDto> getByStatusLimited(DocumentStatus status, Integer limit);
    DocumentResponseDto getByIdWithHistory(Long id);

    List<DocumentResponseDto> search(DocumentFilterDto documentFilterDto);

    Page<DocumentResponseDto> findAllByIds(List<Long> ids, DocumentPageableDto dto);

    DocumentResponseDto create(DocumentRequestDto dto);

    void updateDocumentStatus(Long documentId, DocumentUpdateStatusDto dto);
}
