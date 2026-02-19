package com.ITQGroup.mapper;

import com.ITQGroup.dto.user.UserRequestDto;
import com.ITQGroup.dto.user.UserResponseDto;
import com.ITQGroup.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper
        (
                componentModel = "spring",
                injectionStrategy = InjectionStrategy.CONSTRUCTOR,
                unmappedTargetPolicy = ReportingPolicy.ERROR
        )
public interface UserMapper {

        UserResponseDto toResponseDto(User user);

        User toEntity(UserRequestDto dto);


}
