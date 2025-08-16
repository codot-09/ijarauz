package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.AdminLoginRequest;
import com.example.ijara.dto.request.LoginRequest;
import com.example.ijara.dto.response.LoginResponse;
import com.example.ijara.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autentifikatsiya API",description = "Tizimga kirish uchun endpointlar")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Foydalanuvchilar uchun login",
            description = "Login qismi telegram login widget asosida ishlaydi"
    )
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/admin")
    @Operation(summary = "Admin uchun login")
    public ResponseEntity<ApiResponse<LoginResponse>> adminLogin(
            @RequestBody AdminLoginRequest request
    ){
        return ResponseEntity.ok(authService.adminLogin(request));
    }
}
