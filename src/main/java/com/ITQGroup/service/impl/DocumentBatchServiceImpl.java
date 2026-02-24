package com.ITQGroup.service.impl;

import com.ITQGroup.constant.Constant;
import com.ITQGroup.constant.ExceptionConstant;
import com.ITQGroup.dto.document.DocumentBatchCreateRequestDto;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentShortResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateStatusDto;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.enums.ResponseStatus;
import com.ITQGroup.exception.DocumentProcessingException;
import com.ITQGroup.mapper.DocumentMapper;
import com.ITQGroup.service.DocumentBatchService;
import com.ITQGroup.service.DocumentService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentBatchServiceImpl implements DocumentBatchService {

    private final DocumentService documentService;

    private final DocumentMapper documentMapper;


    @Override
    public Page<DocumentResponseDto> getByListIds(List<Long> ids, DocumentPageableDto pageDto) {

        return documentService.getAllByIds(ids, pageDto);
    }

    @Override
    public List<DocumentShortResponseDto> batchCreate(DocumentBatchCreateRequestDto dto) {

        long start = System.nanoTime();

        log.info("Entering  batchCreate(DocumentBatchCreateRequestDto dto) method");

        List<DocumentRequestDto> documentsToCreate = new ArrayList<>();

        log.info("{} are gonna be created", dto.getDocumentQuantityToCreate());

        for (int i = 0; i < dto.getDocumentQuantityToCreate(); i++) {

            documentsToCreate.add(new DocumentRequestDto(dto.getAuthorId(), Constant.DOCUMENT_BATCH_CREATION_NAME));

            log.info("{} document from {} are added to creation list", i+1, dto.getDocumentQuantityToCreate());
        }

        List<DocumentShortResponseDto> documentShortResponseDtos = documentService.batchCreate(documentsToCreate);

        long end = System.nanoTime();

        log.info("Exit batchCreate(DocumentBatchCreateRequestDto dto) method. Execution time: {}", end-start);

        return documentShortResponseDtos;
    }

    @Override
    public List<DocumentProcessingResultDto> sendBatchSubmitted(Long authorId, List<Long> docsIds) {

        log.info("Entering sendBatchSubmitted(Long authorId, List<Long> docsIds) method");

        log.info("{} documents submitted to consideration", docsIds.size());

        List<DocumentProcessingResultDto> documentProcessingResultDtos = processDocuments(authorId, docsIds, DocumentStatus.DRAFT, DocumentStatus.SUBMITTED);

        log.info("Exit sendBatchSubmitted(Long authorId, List<Long> docsIds) method");

        return documentProcessingResultDtos;
    }

    @Override
    public List<DocumentProcessingResultDto> sendBatchApproved(Long authorId, List<Long> docsIds) {

        log.info("Entering sendBatchApproved(Long authorId ...) method");

        List<DocumentProcessingResultDto> documentProcessingResultDtos = processDocuments(authorId, docsIds, DocumentStatus.SUBMITTED, DocumentStatus.APPROVED);

        log.info("Exit sendBatchApproved(Long authorId ...) method");

        return documentProcessingResultDtos;
    }

    private List<DocumentProcessingResultDto> processDocuments(Long authorId, List<Long> ids, DocumentStatus currentStatus, DocumentStatus newStatus) {

        log.info("Entering processDocuments(Long authorId ...) method");

        List<DocumentProcessingResultDto> responseDtoList = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {

            Long id = ids.get(i);

            try {

                log.info("Trying to update document status by ID: {}", id);

                documentService.updateDocumentStatus(id, new DocumentUpdateStatusDto(authorId, currentStatus, newStatus));

                log.info("Document by ID {} was successfully updated and have status: {}",
                        id, newStatus);

                log.info("{} of {} documents are ready", i + 1, ids.size());

            } catch (DocumentProcessingException e) {

                log.error("Failed to process document with ID: {}, status: {}, message: {}",
                        id, e.getResponseStatus(), e.getMessage(), e);

                responseDtoList.add(documentMapper.constructResultDto(id, e.getResponseStatus(), e.getMessage()));
                continue;

            } catch (OptimisticLockException e) {

                log.error("Failed to update document ID: {}, due the optimistic lock. Message; {}", id, e.getMessage());

                responseDtoList.add(documentMapper.constructResultDto(id, ResponseStatus.CONFLICT.name(), ExceptionConstant.FAILED_UPDATE_DOCUMENT));
                continue;

            } catch (Exception e) {

                log.error("Unexpected exception due the processing document with ID: {}, message: {}", id, e.getMessage());

                responseDtoList.add(documentMapper.constructResultDto(id, ResponseStatus.UNKNOWN_ERROR.name(), ""));
                continue;
            }

            responseDtoList.add(documentMapper.constructResultDto(id, ResponseStatus.SUCCESS.name(), ""));
        }

        log.info("Exit processDocuments(Long authorId ...) method");

        return responseDtoList;

    }
}
