package com.ITQGroup.service;

import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateDto;

public interface DocumentService {

    DocumentResponseDto getById(Long id);

    DocumentResponseDto create(DocumentRequestDto dto);

    void update(Long documentId, DocumentUpdateDto dto);
}
