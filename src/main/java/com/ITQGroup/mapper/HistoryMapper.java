package com.ITQGroup.mapper;

import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.History;
import com.ITQGroup.enums.Action;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR,
                uses = DocumentMapper.class
        )
public interface HistoryMapper {

        History construct(Document document, Long authorId, Action action);
        History construct(Document document, Long authorId, Action action, String comment);

}
