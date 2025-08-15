package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import com.example.ijara.mapper.UserMapper;
import com.example.ijara.repository.UserRepository;
import com.example.ijara.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public ApiResponse<UserResponse> getProfile(User user) {
        return ApiResponse.success(mapper.toResponse(user));
    }
}
