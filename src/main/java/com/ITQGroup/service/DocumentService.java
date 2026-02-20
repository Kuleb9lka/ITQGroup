package com.ITQGroup.service;

import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DocumentService {

    DocumentResponseDto getById(Long id);
    DocumentResponseDto getByIdWithHistory(Long id);

    Page<DocumentResponseDto> findAllByIds(List<Long> ids, DocumentPageableDto dto);

    DocumentResponseDto create(DocumentRequestDto dto);

    void update(Long documentId, DocumentUpdateDto dto);
}
