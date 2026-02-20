package com.ITQGroup.service.impl;

import com.ITQGroup.constant.Constant;
import com.ITQGroup.constant.ExceptionConstant;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateDto;
import com.ITQGroup.entity.ApprovalRegistry;
import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.History;
import com.ITQGroup.enums.Action;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.exception.ApprovalRegistryException;
import com.ITQGroup.exception.DocumentNotFoundException;
import com.ITQGroup.exception.DocumentStatusConflictException;
import com.ITQGroup.exception.StatusNotFoundException;
import com.ITQGroup.mapper.ApprovalRegistryMapper;
import com.ITQGroup.mapper.DocumentMapper;
import com.ITQGroup.mapper.HistoryMapper;
import com.ITQGroup.reposiroty.ApprovalRegistryRepository;
import com.ITQGroup.reposiroty.DocumentRepository;
import com.ITQGroup.reposiroty.HistoryRepository;
import com.ITQGroup.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final DocumentMapper documentMapper;

    private final HistoryRepository historyRepository;

    private final HistoryMapper historyMapper;

    private final ApprovalRegistryRepository registryRepository;

    private final ApprovalRegistryMapper registryMapper;


    @Override
    public DocumentResponseDto getById(Long id) {

        Document documentById = getDocumentById(id);

        return documentMapper.toResponseDto(documentById);
    }

    @Override
    public DocumentResponseDto getByIdWithHistory(Long id) {

        Document document = documentRepository.findByIdWithHistory(id).orElseThrow(() ->
                new DocumentNotFoundException(ExceptionConstant.DOCUMENT_NOT_FOUND_BY_ID + id, Constant.RESPONSE_STATUS_NOT_FOUND));

        return documentMapper.toResponseDto(document);
    }

    @Override
    public Page<DocumentResponseDto> findAllByIds(List<Long> ids, DocumentPageableDto dto) {

        Pageable pageable = constructPageable(dto);

        Page<Document> allByIds = documentRepository.findAllByIdIn(ids, pageable);

        return allByIds.map(documentMapper::toResponseDto);
    }


    @Override
    public DocumentResponseDto create(DocumentRequestDto dto) {

        Document document =
                documentMapper.toEntityFromRequestDto(dto);

        document.setCreateDate(LocalDateTime.now());
        document.setUniqueNumber(UUID.randomUUID());
        document.setStatus(DocumentStatus.DRAFT);

        Document savedDocument = documentRepository.save(document);

        return documentMapper.toResponseDto(savedDocument);
    }

    @Override
    @Transactional
    public void update(Long documentId, DocumentUpdateDto dto) {

        Long authorId = dto.getAuthorId();

        Document documentById = getDocumentById(documentId);

        DocumentStatus currentDocStatus = documentById.getStatus();

        checkStatusConflict(currentDocStatus, dto.getOldStatus());

        documentMapper.updateFromDb(dto, documentById);

        documentById.setUpdateDate(LocalDateTime.now());

        Action actionByStatus = getActionByStatus(dto.getOldStatus());

        History constructedHistory =
                historyMapper.construct(documentById, authorId, actionByStatus);

        History savedHistory = saveHistory(constructedHistory);

        if (documentById.getStatus().equals(DocumentStatus.APPROVED)) {
            try {
                ApprovalRegistry constructedRegistry =
                        registryMapper.construct(documentById, authorId, savedHistory.getUpdateDate());

                registryRepository.save(constructedRegistry);
            } catch (Exception e){

                throw new ApprovalRegistryException(ExceptionConstant.FAILED_WRITE_DOCUMENT_REGISTRY + documentById.getId(), Constant.RESPONSE_STATUS_APPROVAL_REGISTRY_ERROR);
            }
        }
    }


    private History saveHistory(History history){

        return historyRepository.save(history);
    }

    private Document getDocumentById(Long id) {

        return documentRepository.findById(id).orElseThrow(() ->
                new DocumentNotFoundException(ExceptionConstant.DOCUMENT_NOT_FOUND_BY_ID + id, Constant.RESPONSE_STATUS_NOT_FOUND));
    }

    private Action getActionByStatus(DocumentStatus status) {

        if (!Constant.DOCUMENT_ACTIONS_MAP.containsKey(status)) {

            throw new StatusNotFoundException(ExceptionConstant.STATUS_NOT_FOUND + status, Constant.RESPONSE_STATUS_NOT_FOUND);
        }

        return Constant.DOCUMENT_ACTIONS_MAP.get(status);
    }

    private Pageable constructPageable(DocumentPageableDto dto){

        Sort sort = dto.getAsc() ?
                Sort.by(dto.getSortBy()).ascending() :
                Sort.by(dto.getSortBy()).descending();

        return PageRequest.of(dto.getPage(), dto.getSize(), sort);
    }

    private void checkStatusConflict(DocumentStatus currentDocStatus, DocumentStatus oldStatus){

        if (!currentDocStatus.equals(oldStatus)){

            throw new DocumentStatusConflictException(ExceptionConstant.DOCUMENT_STATUS_CONFLICT + currentDocStatus, Constant.RESPONSE_STATUS_CONFLICT);
        }
    }
}
