package com.ITQGroup.mapper;

import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.dto.document.DocumentUpdateDto;
import com.ITQGroup.entity.Document;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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


        @Mapping(target = "id", ignore = true)
        @Mapping(target = "uniqueNumber", ignore = true)
        @Mapping(target = "status", ignore = true)
        @Mapping(target = "createDate", ignore = true)
        @Mapping(target = "updateDate", ignore = true)
        @Mapping(target = "historyList", ignore = true)
        Document toEntityFromRequestDto(DocumentRequestDto dto);


        @Mapping(target = "id", ignore = true)
        @Mapping(target = "name", ignore = true)
        @Mapping(target = "uniqueNumber", ignore = true)
        @Mapping(target = "status", source = "newStatus")
        @Mapping(target = "createDate", ignore = true)
        @Mapping(target = "updateDate", ignore = true)
        @Mapping(target = "historyList", ignore = true)
        void updateFromDb(DocumentUpdateDto dto, @MappingTarget Document document);
}
