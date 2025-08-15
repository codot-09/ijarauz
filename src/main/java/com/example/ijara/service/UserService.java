package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;

public interface UserService {
    ApiResponse<UserResponse> getProfile(User user);
}
