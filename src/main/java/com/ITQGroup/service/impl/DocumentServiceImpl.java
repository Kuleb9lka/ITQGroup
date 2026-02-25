package com.ITQGroup.service.impl;

import com.ITQGroup.constant.Constant;
import com.ITQGroup.constant.ExceptionConstant;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentShortResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateStatusDto;
import com.ITQGroup.dto.filter.DocumentFilterDto;
import com.ITQGroup.dto.specification.DocumentSpecification;
import com.ITQGroup.entity.ApprovalRegistry;
import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.History;
import com.ITQGroup.enums.Action;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.enums.ResponseStatus;
import com.ITQGroup.exception.DocumentNotFoundException;
import com.ITQGroup.exception.DocumentStatusConflictException;
import com.ITQGroup.exception.StatusNotFoundException;
import com.ITQGroup.mapper.ApprovalRegistryMapper;
import com.ITQGroup.mapper.DocumentMapper;
import com.ITQGroup.mapper.HistoryMapper;
import com.ITQGroup.reposiroty.ApprovalRegistryRepository;
import com.ITQGroup.reposiroty.DocumentRepository;
import com.ITQGroup.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final DocumentMapper documentMapper;

    private final HistoryMapper historyMapper;

    private final ApprovalRegistryRepository registryRepository;

    private final ApprovalRegistryMapper registryMapper;


    @Override
    public DocumentResponseDto getById(Long id) {

        Document documentById = getDocumentById(id);

        return documentMapper.toResponseDto(documentById);
    }

    @Override
    public List<DocumentShortResponseDto> getByStatusLimited(DocumentStatus status, Integer limit) {

        Pageable limited = PageRequest.of(0, limit);
        List<Document> documentsByStatusAndLimit = documentRepository.findByStatusLimited(status, limited);

        return documentMapper.toShortResponseList(documentsByStatusAndLimit);
    }

    @Override
    public DocumentResponseDto getByIdWithHistory(Long id) {

        Document document = documentRepository.findByIdWithHistory(id).orElseThrow(() ->
                new DocumentNotFoundException(ExceptionConstant.DOCUMENT_NOT_FOUND_BY_ID + id, ResponseStatus.NOT_FOUND.name()));

        return documentMapper.toResponseDto(document);
    }

    @Override
    public List<DocumentResponseDto> search(DocumentFilterDto documentFilterDto) {

        Specification<Document> documentSpecification = DocumentSpecification.withFilters(documentFilterDto);

        List<Document> documentsWithSpecification = documentRepository.findAll(documentSpecification);

        return documentMapper.toResponseList(documentsWithSpecification);
    }

    @Override
    public Page<DocumentResponseDto> getAllByIds(List<Long> ids, DocumentPageableDto dto) {

        Pageable pageable = constructPageable(dto);

        Page<Document> allByIds = documentRepository.findAllByIdIn(ids, pageable);

        return allByIds.map(documentMapper::toResponseDto);
    }

    @Override
    @Transactional
    public List<DocumentShortResponseDto> batchCreate(List<DocumentRequestDto> list) {

        log.info("Entering batchCreate(List<DocumentRequestDto> list) method");

        List<Document> documents = constructAndMapBatch(list);

        log.info("Trying to save all document batch");

        List<Document> savedDocuments = documentRepository.saveAll(documents);

        log.info("Exit batchCreate(List<DocumentRequestDto> list) method");

        return documentMapper.toShortResponseList(savedDocuments);
    }


    @Override
    public DocumentResponseDto create(DocumentRequestDto dto) {

        log.info("Entering  create(DocumentRequestDto dto) method");

        long start = System.nanoTime();

        Document document =
                documentMapper.toEntityFromRequestDto(dto);

        documentMapper.fillAdditionalInfo(document, UUID.randomUUID(), DocumentStatus.DRAFT, LocalDateTime.now());

        log.info("Trying to save new document");

        Document savedDocument = documentRepository.save(document);

        log.info("Document was successfully saved");

        long end = System.nanoTime();

        log.info("Exit create(DocumentRequestDto dto) method. Execution time: {}", end - start);

        return documentMapper.toResponseDto(savedDocument);
    }

    @Override
    @Transactional
    public void updateDocumentStatus(Long documentId, DocumentUpdateStatusDto dto) {

        log.info("Entering  updateDocumentStatus(Long documentId ...) method");

        Long authorId = dto.getAuthorId();

        Document documentById = getDocumentById(documentId);

        DocumentStatus currentDocStatus = documentById.getStatus();

        checkStatusConflict(currentDocStatus, dto.getOldStatus());

        documentMapper.updateFromDb(dto, documentById);

        documentById.setUpdateDate(LocalDateTime.now());

        Action actionByStatus = getActionByStatus(dto.getOldStatus());

        History constructedHistory =
                historyMapper.construct(documentById, authorId, actionByStatus, LocalDateTime.now());

        documentById.getHistoryList().add(constructedHistory);

        log.info("Trying to save updated document with history. Document ID: {}, author ID: {}", documentById, authorId);

        documentRepository.save(documentById);

        log.info("Document with ID {} was successfully updated", documentById);

        if (documentById.getStatus().equals(DocumentStatus.APPROVED)) {

            log.info("Trying to save approved document to Approval Registry");

            ApprovalRegistry constructedRegistry =
                    registryMapper.construct(documentById, authorId, constructedHistory.getUpdateDate());

            log.info("Trying to save approval registry entity with document ID: {}, author ID: {}", documentById, authorId);

            registryRepository.save(constructedRegistry);

            log.info("Approval registry entity was successfully saved");
        }

        log.info("Exit updateDocumentStatus(Long documentId ...) method");
    }

    private List<Document> constructAndMapBatch(List<DocumentRequestDto> requestDtos) {

        log.info("Entering constructAndMapBatch(List<DocumentRequestDto> requestDtos) method");

        List<Document> documents = requestDtos.stream()
                .map(dto -> {

                    Document document = documentMapper.toEntityFromRequestDto(dto);

                    documentMapper.fillAdditionalInfo(document, UUID.randomUUID(), DocumentStatus.DRAFT, LocalDateTime.now());

                    return document;
                })
                .toList();

        log.info("Exit constructAndMapBatch(List<DocumentRequestDto> requestDtos) method");

        return documents;
    }


    private Document getDocumentById(Long id) {

        log.info("Entering getDocumentById(Long id) method");

        Document document = documentRepository.findById(id).orElseThrow(() ->
                new DocumentNotFoundException(ExceptionConstant.DOCUMENT_NOT_FOUND_BY_ID + id, ResponseStatus.NOT_FOUND.name()));

        log.info("Document by ID {} successfully got", id);

        log.info("Exit getDocumentById(Long id) method");
        return document;
    }

    private Action getActionByStatus(DocumentStatus status) {

        log.info("Entering etActionByStatus(DocumentStatus status) method");

        if (!Constant.DOCUMENT_ACTIONS_MAP.containsKey(status)) {

            log.error("Document status not found {}", status);

            throw new StatusNotFoundException(ExceptionConstant.STATUS_NOT_FOUND + status, ResponseStatus.NOT_FOUND.name());
        }

        log.info("Exit etActionByStatus(DocumentStatus status) method");

        return Constant.DOCUMENT_ACTIONS_MAP.get(status);
    }

    private Pageable constructPageable(DocumentPageableDto dto) {

        Sort sort = dto.getAsc() ?
                Sort.by(dto.getSortBy()).ascending() :
                Sort.by(dto.getSortBy()).descending();

        return PageRequest.of(dto.getPage(), dto.getSize(), sort);
    }

    private void checkStatusConflict(DocumentStatus currentDocStatus, DocumentStatus oldStatus) {

        log.info("Entering checkStatusConflict(DocumentStatus currentDocStatus ...) method");

        if (!currentDocStatus.equals(oldStatus)) {

            log.error("Document has already status {}", currentDocStatus);

            throw new DocumentStatusConflictException(ExceptionConstant.DOCUMENT_STATUS_CONFLICT + currentDocStatus, ResponseStatus.CONFLICT.name());
        }

        log.info("Exit checkStatusConflict(DocumentStatus currentDocStatus ...) method");
    }
}
