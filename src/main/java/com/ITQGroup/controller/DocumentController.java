package com.ITQGroup.controller;

import com.ITQGroup.api.DocumentApi;
import com.ITQGroup.dto.PageResponseDto;
import com.ITQGroup.dto.document.DocumentBatchCreateRequestDto;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentShortResponseDto;
import com.ITQGroup.dto.filter.DocumentFilterDto;
import com.ITQGroup.service.DocumentBatchService;
import com.ITQGroup.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
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
@Slf4j
@Validated
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;

    private final DocumentBatchService documentBatchService;


    @PostMapping("/create")
    public DocumentResponseDto create(@RequestBody DocumentRequestDto dto) {

        log.info("Entering create(DocumentRequestDto dto) controller method");

        DocumentResponseDto documentResponseDto = documentService.create(dto);

        log.info("Exit create(DocumentRequestDto dto) controller method");

        return documentResponseDto;
    }

    @PostMapping("/batch-create")
    public List<DocumentShortResponseDto> batchCreate(@RequestBody DocumentBatchCreateRequestDto dto) {

        log.info("Entering batchCreate(DocumentBatchCreateRequestDto dto) controller method");
        List<DocumentShortResponseDto> documentShortResponseDtos = documentBatchService.batchCreate(dto);
        log.info("Exit batchCreate(DocumentBatchCreateRequestDto dto) controller method");
        return documentShortResponseDtos;
    }

    @GetMapping("/{id}")
    public DocumentResponseDto getById(@PathVariable Long id) {

        return documentService.getByIdWithHistory(id);
    }

    @PostMapping("/search")
    public List<DocumentResponseDto> search(@RequestBody DocumentFilterDto filterDto) {

        return documentService.search(filterDto);
    }

    @PostMapping("/filter")
    public PageResponseDto<DocumentResponseDto> filterWithPageable(@RequestBody List<Long> documentIds, DocumentPageableDto pageableSettings) {

        Page<DocumentResponseDto> page = documentBatchService.getByListIds(documentIds, pageableSettings);

        return new PageResponseDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    @PostMapping("/send-submission/{authorId}")
    public List<DocumentProcessingResultDto> sendToSubmission(@PathVariable Long authorId,
                                                              @RequestBody List<Long> ids) {

        log.info("Entering sendToSubmitted(Long authorId ...) method");

        List<DocumentProcessingResultDto> documentProcessingResultDtos = documentBatchService.sendBatchSubmitted(authorId, ids);

        log.info("Exit sendToSubmitted(Long authorId ...) controller method");

        return documentProcessingResultDtos;
    }

    @PostMapping("/send-approval/{authorId}")
    public List<DocumentProcessingResultDto> sendToApproval(@PathVariable Long authorId,
                                                            @RequestBody List<Long> ids) {

        log.info("Entering sendToApproval(Long authorId ...) controller method");

        List<DocumentProcessingResultDto> documentProcessingResultDtos = documentBatchService.sendBatchApproved(authorId, ids);

        log.info("Exit sendToApproval(Long authorId ...) controller method");

        return documentProcessingResultDtos;
    }


}
