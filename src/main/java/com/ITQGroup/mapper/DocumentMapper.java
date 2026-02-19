package com.ITQGroup.mapper;

import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateDto;
import com.ITQGroup.entity.Document;
import com.ITQGroup.enums.DocumentStatus;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR,
                uses = HistoryMapper.class
        )
public interface DocumentMapper {

        DocumentResponseDto toResponseDto(Document document);

        Document construct(Long authorId, String name, DocumentStatus status);

        void updateFromDb(DocumentUpdateDto dto, @MappingTarget Document document);
}
