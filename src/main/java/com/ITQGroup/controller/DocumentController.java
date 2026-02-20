package com.ITQGroup.controller;

import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.PageResponseDto;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.filter.DocumentFilterDto;
import com.ITQGroup.service.BatchDocumentService;
import com.ITQGroup.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    private final BatchDocumentService batchDocumentService;


    @PostMapping("/create")
    public DocumentResponseDto create(@RequestBody @Valid DocumentRequestDto dto) {

        return documentService.create(dto);
    }

    @GetMapping("/{id}")
    public DocumentResponseDto getById(@PathVariable Long id) {

        return documentService.getByIdWithHistory(id);
    }

    @PostMapping("/search")
    public List<DocumentResponseDto> search(@RequestBody DocumentFilterDto filterDto){

        return documentService.search(filterDto);
    }

    @PostMapping("/filter")
    public PageResponseDto<DocumentResponseDto> getAllByListId(@RequestBody List<Long> documentIds, @Valid DocumentPageableDto pageableSettings) {

        Page<DocumentResponseDto> page = batchDocumentService.getByListIds(documentIds, pageableSettings);

        return new PageResponseDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    @PostMapping("/send-submitted/{authorId}")
    public List<DocumentProcessingResultDto> sendToSubmitted(@RequestBody List<Long> ids, @PathVariable Long authorId) {

        return batchDocumentService.sendBatchSubmitted(authorId, ids);
    }

    @PostMapping("/send-approval/{authorId}")
    public List<DocumentProcessingResultDto> sendToApproval(@RequestBody List<Long> ids, @PathVariable Long authorId) {

        return batchDocumentService.sendBatchApproved(authorId, ids);
    }


}
