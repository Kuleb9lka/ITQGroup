package com.ITQGroup.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR,
                uses = {HistoryMapper.class, UserMapper.class}
        )
public interface DocumentMapper {
}
