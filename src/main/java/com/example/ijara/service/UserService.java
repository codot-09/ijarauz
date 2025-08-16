package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    ApiResponse<UserResponse> getProfile(User user);
    ApiResponse<Page<UserResponse>> search(String field, UserRole role, Pageable pageable);
    ApiResponse<UserResponse> getById(UUID id);
}
