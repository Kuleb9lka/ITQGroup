package com.ITQGroup.service.impl;

import com.ITQGroup.constant.Constant;
import com.ITQGroup.constant.ExceptionConstant;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateDto;
import com.ITQGroup.entity.ApprovalRegistry;
import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.History;
import com.ITQGroup.enums.Action;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.exception.DocumentNotFoundException;
import com.ITQGroup.exception.StatusNotFoundException;
import com.ITQGroup.mapper.ApprovalRegistryMapper;
import com.ITQGroup.mapper.DocumentMapper;
import com.ITQGroup.mapper.HistoryMapper;
import com.ITQGroup.reposiroty.ApprovalRegistryRepository;
import com.ITQGroup.reposiroty.DocumentRepository;
import com.ITQGroup.reposiroty.HistoryRepository;
import com.ITQGroup.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public DocumentResponseDto create(DocumentRequestDto dto) {

        Document constructedDocument =
                documentMapper.construct(dto.getAuthorId(), dto.getName(), DocumentStatus.DRAFT);

        Document savedDocument = documentRepository.save(constructedDocument);

        return documentMapper.toResponseDto(savedDocument);
    }

    @Override
    @Transactional
    public void update(Long documentId, DocumentUpdateDto dto) {

        Document documentById = getDocumentById(documentId);
        documentMapper.updateFromDb(dto, documentById);
        Document savedDocument = documentRepository.save(documentById);

        Action actionByStatus = getActionByStatus(dto.getStatus());
        History constructedHistory =
                historyMapper.construct(documentById, dto.getAuthorId(), actionByStatus);
        History savedHistory = historyRepository.save(constructedHistory);

        if (dto.getStatus().equals(DocumentStatus.APPROVED)){

            ApprovalRegistry constructedRegistry =
                    registryMapper.construct(savedDocument, dto.getAuthorId(), savedHistory.getUpdateDate());

            registryRepository.save(constructedRegistry);
        }
    }

    private Document getDocumentById(Long id){

        return documentRepository.findById(id).orElseThrow(() ->
                new DocumentNotFoundException(ExceptionConstant.DOCUMENT_NOT_FOUND_BY_ID + id));
    }

    private Action getActionByStatus(DocumentStatus status){

        if (!Constant.DOCUMENT_ACTIONS_MAP.containsKey(status)){

            throw new StatusNotFoundException(ExceptionConstant.STATUS_NOT_FOUND + status);
        }

        return Constant.DOCUMENT_ACTIONS_MAP.get(status);
    }
}
