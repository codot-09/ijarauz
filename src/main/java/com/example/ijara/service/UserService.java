package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.UpdateProfileRequest;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    ApiResponse<UserResponse> getProfile(User user);

    ApiResponse<Page<UserResponse>> searchUsers(String query, UserRole role, Pageable pageable);

    ApiResponse<UserResponse> getUserById(UUID userId);

    ApiResponse<String> updateProfile(User user, UpdateProfileRequest req);

    ApiResponse<String> blockUser(UUID userId);

    ApiResponse<String> unblockUser(UUID userId);

    ApiResponse<String> changeRole(User user,UUID targetId, UserRole newRole);

    User getCurrentUser();
}