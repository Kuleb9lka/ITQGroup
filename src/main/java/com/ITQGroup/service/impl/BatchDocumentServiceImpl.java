package com.ITQGroup.service.impl;

import com.ITQGroup.constant.Constant;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateDto;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.exception.DocumentProcessingException;
import com.ITQGroup.service.BatchDocumentService;
import com.ITQGroup.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchDocumentServiceImpl implements BatchDocumentService {

    private final DocumentService documentService;


    @Override
    public Page<DocumentResponseDto> getByListIds(List<Long> ids, DocumentPageableDto pageDto) {

        return documentService.findAllByIds(ids, pageDto);
    }

    @Override
    public List<DocumentProcessingResultDto> sendBatchSubmitted(Long authorId, List<Long> docsIds) {

        return processDocuments(authorId, docsIds, DocumentStatus.DRAFT, DocumentStatus.SUBMITTED);
    }

    @Override
    public List<DocumentProcessingResultDto> sendBatchApproved(Long authorId, List<Long> docsIds) {

        return processDocuments(authorId, docsIds, DocumentStatus.SUBMITTED, DocumentStatus.APPROVED);
    }

    private List<DocumentProcessingResultDto> processDocuments(Long authorId, List<Long> ids, DocumentStatus currentStatus, DocumentStatus newStatus){

        List<DocumentProcessingResultDto> responseDtoList = new ArrayList<>();

        for (Long id : ids) {

            try {

                documentService.update(id, new DocumentUpdateDto(authorId, currentStatus, newStatus));

            } catch (DocumentProcessingException e){

                responseDtoList.add(constructResponseDto(id, e.getResponseStatus(), e.getMessage()));

            } catch (Exception e){

                responseDtoList.add(constructResponseDto(id, Constant.RESPONSE_STATUS_UNKNOWN_ERROR, ""));
                continue;
            }

            responseDtoList.add(constructResponseDto(id, Constant.RESPONSE_STATUS_SUCCESS, ""));
        }

        return responseDtoList;

    }

    private DocumentProcessingResultDto constructResponseDto(Long id, String status, String result){

        return new DocumentProcessingResultDto(id, status, result);
    }
}
