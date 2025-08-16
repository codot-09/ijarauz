package com.example.ijara.service.impl;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.exception.DataNotFoundException;
import com.example.ijara.mapper.UserMapper;
import com.example.ijara.repository.UserRepository;
import com.example.ijara.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public ApiResponse<UserResponse> getProfile(User user) {
        return ApiResponse.success(mapper.toResponse(user));
    }

    @Override
    public ApiResponse<Page<UserResponse>> search(String field, UserRole role, Pageable pageable) {
        Page<User> users = userRepository.search(field,role.name(),pageable);
        Page<UserResponse> responsePage = users.map(mapper::toResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<UserResponse> getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Foydalanuvchi topilmadi"));
        return ApiResponse.success(mapper.toResponse(user));
    }
}
