package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.LoginRequest;
import com.example.ijara.dto.request.RegisterRequest;
import com.example.ijara.dto.request.TelegramLoginRequest;
import com.example.ijara.dto.response.LoginResponse;

public interface AuthService {
    ApiResponse<LoginResponse> telegramLogin(TelegramLoginRequest request);
    ApiResponse<String> register(RegisterRequest request);
    ApiResponse<LoginResponse> login(LoginRequest request);
    ApiResponse<LoginResponse> verifyEmail(Integer code);

    ApiResponse<String> resetPassword(String email);

    ApiResponse<String> checkCode(Integer code);
    ApiResponse<String> setPassword(String newPassword,String email);
}
