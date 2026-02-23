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
import com.ITQGroup.exception.ApprovalRegistryException;
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

        Document document =
                documentMapper.toEntityFromRequestDto(dto);

        documentMapper.fillAdditionalInfo(document, UUID.randomUUID(), DocumentStatus.DRAFT, LocalDateTime.now());

        Document savedDocument = documentRepository.save(document);

        return documentMapper.toResponseDto(savedDocument);
    }

    @Override
    @Transactional
    public void updateDocumentStatus(Long documentId, DocumentUpdateStatusDto dto) {

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

        documentRepository.save(documentById);

        if (documentById.getStatus().equals(DocumentStatus.APPROVED)) {
            try {
                ApprovalRegistry constructedRegistry =
                        registryMapper.construct(documentById, authorId, constructedHistory.getUpdateDate());

                registryRepository.save(constructedRegistry);
            } catch (Exception e) {

                throw new ApprovalRegistryException(ExceptionConstant.FAILED_WRITE_DOCUMENT_REGISTRY + documentById.getId(), ResponseStatus.APPROVAL_REGISTRY_ERROR.name());
            }
        }
    }

    private List<Document> constructAndMapBatch(List<DocumentRequestDto> requestDtos){

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

        return documentRepository.findById(id).orElseThrow(() ->
                new DocumentNotFoundException(ExceptionConstant.DOCUMENT_NOT_FOUND_BY_ID + id, ResponseStatus.NOT_FOUND.name()));
    }

    private Action getActionByStatus(DocumentStatus status) {

        if (!Constant.DOCUMENT_ACTIONS_MAP.containsKey(status)) {

            throw new StatusNotFoundException(ExceptionConstant.STATUS_NOT_FOUND + status, ResponseStatus.NOT_FOUND.name());
        }

        return Constant.DOCUMENT_ACTIONS_MAP.get(status);
    }

    private Pageable constructPageable(DocumentPageableDto dto) {

        Sort sort = dto.getAsc() ?
                Sort.by(dto.getSortBy()).ascending() :
                Sort.by(dto.getSortBy()).descending();

        return PageRequest.of(dto.getPage(), dto.getSize(), sort);
    }

    private void checkStatusConflict(DocumentStatus currentDocStatus, DocumentStatus oldStatus) {

        if (!currentDocStatus.equals(oldStatus)) {

            throw new DocumentStatusConflictException(ExceptionConstant.DOCUMENT_STATUS_CONFLICT + currentDocStatus, ResponseStatus.CONFLICT.name());
        }
    }
}
