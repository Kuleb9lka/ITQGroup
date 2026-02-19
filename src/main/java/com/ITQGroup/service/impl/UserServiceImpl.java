package com.ITQGroup.service.impl;

import com.ITQGroup.constant.ExceptionConstant;
import com.ITQGroup.dto.user.UserRequestDto;
import com.ITQGroup.dto.user.UserResponseDto;
import com.ITQGroup.entity.User;
import com.ITQGroup.exception.UserAlreadyExistException;
import com.ITQGroup.exception.UserNotFoundException;
import com.ITQGroup.mapper.UserMapper;
import com.ITQGroup.reposiroty.UserRepository;
import com.ITQGroup.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponseDto getById(Long id) {

        User userById = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(ExceptionConstant.USER_NOT_FOUND_BY_ID + id));

        return userMapper.toResponseDto(userById);
    }

    @Override
    public UserResponseDto create(UserRequestDto dto) {

        userRepository.findByLogin(dto.getLogin()).ifPresent(user -> {
            throw new UserAlreadyExistException(ExceptionConstant.LOGIN_IS_ALREADY_EXIST + user.getLogin());
        });

        User user = userMapper.toEntity(dto);

        User savedUser = userRepository.save(user);

        return userMapper.toResponseDto(savedUser);
    }
}
