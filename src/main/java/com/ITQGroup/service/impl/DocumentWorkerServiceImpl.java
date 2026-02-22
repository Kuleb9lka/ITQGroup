package com.ITQGroup.service.impl;

import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.service.DocumentBatchService;
import com.ITQGroup.service.DocumentService;
import com.ITQGroup.service.DocumentWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentWorkerServiceImpl implements DocumentWorkerService {

    private final DocumentBatchService documentBatchService;

    private final DocumentService documentService;

    @Override
    public void processByStatus(DocumentStatus status, Integer docsLimit) {

        List<DocumentResponseDto> byStatusLimited = documentService.getByStatusLimited(status, docsLimit);

        List<Long> documentsIds = byStatusLimited.stream()
                .map(DocumentResponseDto::getId).toList();

        documentBatchService.sendBatchApproved(0L, documentsIds);
    }
}
