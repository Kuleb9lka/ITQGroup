package com.ITQGroup.service.impl;

import com.ITQGroup.dto.document.DocumentShortResponseDto;
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
    public void processSubmission(Integer docsLimit) {

        List<DocumentShortResponseDto> docsByStatusAndLimit = getDocsByStatusAndLimit(DocumentStatus.DRAFT, docsLimit);

        List<Long> documentIds = extractDocsIds(docsByStatusAndLimit);

        documentBatchService.sendBatchSubmitted(1L, documentIds);
    }

    @Override
    public void processApproval(Integer docsLimit) {

        List<DocumentShortResponseDto> docsByStatusAndLimit = getDocsByStatusAndLimit(DocumentStatus.SUBMITTED, docsLimit);

        List<Long> documentIds = extractDocsIds(docsByStatusAndLimit);

        documentBatchService.sendBatchApproved(1L, documentIds);
    }

    private List<DocumentShortResponseDto> getDocsByStatusAndLimit(DocumentStatus status, Integer limit){

        return documentService.getByStatusLimited(status, limit);
    }

    private List<Long> extractDocsIds(List<DocumentShortResponseDto> list){

        return list.stream()
                .map(DocumentShortResponseDto::getId).toList();
    }
}
