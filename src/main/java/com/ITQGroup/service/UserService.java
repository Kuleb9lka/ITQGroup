package com.ITQGroup.service;

import com.ITQGroup.dto.user.UserRequestDto;
import com.ITQGroup.dto.user.UserResponseDto;

public interface UserService {

    UserResponseDto getById(Long id);

    UserResponseDto create(UserRequestDto dto);

}
