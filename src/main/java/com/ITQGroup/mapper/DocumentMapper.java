package com.ITQGroup.mapper;

import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentShortResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateStatusDto;
import com.ITQGroup.entity.Document;
import com.ITQGroup.enums.DocumentStatus;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR,
                uses = HistoryMapper.class
        )
public interface DocumentMapper {

        DocumentResponseDto toResponseDto(Document document);

        DocumentShortResponseDto toShortResponseDto(Document document);


        @Mapping(target = "id", ignore = true)
        @Mapping(target = "uniqueNumber", ignore = true)
        @Mapping(target = "status", ignore = true)
        @Mapping(target = "createDate", ignore = true)
        @Mapping(target = "updateDate", ignore = true)
        @Mapping(target = "historyList", ignore = true)
        @Mapping(target = "version", ignore = true)
        Document toEntityFromRequestDto(DocumentRequestDto dto);


        @Mapping(target = "id", ignore = true)
        @Mapping(target = "name", ignore = true)
        @Mapping(target = "uniqueNumber", ignore = true)
        @Mapping(target = "status", source = "newStatus")
        @Mapping(target = "createDate", ignore = true)
        @Mapping(target = "updateDate", ignore = true)
        @Mapping(target = "historyList", ignore = true)
        @Mapping(target = "version", ignore = true)
        void updateFromDb(DocumentUpdateStatusDto dto, @MappingTarget Document document);

        List<DocumentResponseDto> toResponseList(List<Document> documentList);

        List<DocumentShortResponseDto> toShortResponseList(List<Document> documentList);

        DocumentProcessingResultDto constructResultDto(Long documentId, String status, String responseMessage);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "authorId", ignore = true)
        @Mapping(target = "name", ignore = true)
        @Mapping(target = "updateDate", ignore = true)
        @Mapping(target = "historyList", ignore = true)
        @Mapping(target = "version", ignore = true)
        void fillAdditionalInfo(@MappingTarget Document document, UUID uniqueNumber, DocumentStatus status, LocalDateTime createDate);



}
