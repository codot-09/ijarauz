package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.LoginRequest;
import com.example.ijara.dto.response.LoginResponse;

public interface AuthService {
    ApiResponse<LoginResponse> login(LoginRequest request);
}
