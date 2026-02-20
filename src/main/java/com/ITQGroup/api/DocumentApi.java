package com.ITQGroup.api;

import com.ITQGroup.dto.PageResponseDto;
import com.ITQGroup.dto.document.DocumentPageableDto;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.filter.DocumentFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Documents", description = "Operations for managing documents")
public interface DocumentApi {

    @Operation(
            summary = "Create document",
            description = "Create a new document."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    DocumentResponseDto create(DocumentRequestDto dto);


    @Operation(
            summary = "Get document by ID",
            description = "Retrieve a document with its history by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "Document not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    DocumentResponseDto getById(Long id);


    @Operation(
            summary = "Search documents",
            description = "Search documents using filter criteria."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    List<DocumentResponseDto> search(DocumentFilterDto filterDto);


    @Operation(
            summary = "Get documents by ID list with pagination",
            description = "Retrieve documents by list of IDs with pagination support."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or ID list."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    PageResponseDto<DocumentResponseDto> getAllByListId(
            List<Long> documentIds,
            DocumentPageableDto pageableSettings
    );


    @Operation(
            summary = "Send documents to SUBMITTED",
            description = "Batch update documents to SUBMITTED status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch processing completed."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "404", description = "One or more documents not found."),
            @ApiResponse(responseCode = "409", description = "Document status conflict."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    List<DocumentProcessingResultDto> sendToSubmitted(
            Long authorId,
            List<Long> ids
    );


    @Operation(
            summary = "Send documents to APPROVAL",
            description = "Batch update documents to APPROVAL status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch processing completed."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "404", description = "One or more documents not found."),
            @ApiResponse(responseCode = "409", description = "Document status conflict."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    List<DocumentProcessingResultDto> sendToApproval(
            Long authorId,
            List<Long> ids
    );
}
