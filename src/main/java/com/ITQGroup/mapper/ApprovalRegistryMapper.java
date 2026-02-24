package com.ITQGroup.mapper;

import com.ITQGroup.entity.ApprovalRegistry;
import com.ITQGroup.entity.Document;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR,
                uses = DocumentMapper.class
        )
public interface ApprovalRegistryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "document", source = "document")
    ApprovalRegistry construct(Document document, Long authorId, LocalDateTime approvedAt);
}
