package com.ITQGroup.mapper;

import com.ITQGroup.dto.document.DocumentRequestDto;
import com.ITQGroup.dto.document.DocumentResponseDto;
import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.User;
import com.ITQGroup.enums.DocumentStatus;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

        Document toEntity(DocumentRequestDto dto);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "uniqueNumber", ignore = true)
        @Mapping(target = "createDate", ignore = true)
        @Mapping(target = "updateDate", ignore = true)
        Document construct(User user, String name, DocumentStatus status);
}
