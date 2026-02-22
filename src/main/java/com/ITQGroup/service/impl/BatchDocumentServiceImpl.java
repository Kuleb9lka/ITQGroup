package com.ITQGroup.service.impl;

import com.ITQGroup.constant.ExceptionConstant;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateStatusDto;
import com.ITQGroup.enums.ResponseStatus;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.exception.DocumentProcessingException;
import com.ITQGroup.mapper.DocumentMapper;
import com.ITQGroup.service.BatchDocumentService;
import com.ITQGroup.service.DocumentService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchDocumentServiceImpl implements BatchDocumentService {

    private final DocumentService documentService;

    private final DocumentMapper documentMapper;


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

    private List<DocumentProcessingResultDto> processDocuments(Long authorId, List<Long> ids, DocumentStatus currentStatus, DocumentStatus newStatus) {

        List<DocumentProcessingResultDto> responseDtoList = new ArrayList<>();

        for (Long id : ids) {

            try {

                documentService.updateDocumentStatus(id, new DocumentUpdateStatusDto(authorId, currentStatus, newStatus));

            } catch (DocumentProcessingException e) {

                responseDtoList.add(documentMapper.constructResultDto(id, e.getResponseStatus(), e.getMessage()));
                continue;

            } catch (OptimisticLockException e) {

                responseDtoList.add(documentMapper.constructResultDto(id, ResponseStatus.CONFLICT.name(), ExceptionConstant.FAILED_UPDATE_DOCUMENT));
                continue;

            } catch (Exception e) {

                responseDtoList.add(documentMapper.constructResultDto(id, ResponseStatus.UNKNOWN_ERROR.name(), ""));
                continue;
            }

            responseDtoList.add(documentMapper.constructResultDto(id, ResponseStatus.SUCCESS.name(), ""));
        }

        return responseDtoList;

    }
}
