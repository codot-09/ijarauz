package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.AdminLoginRequest;
import com.example.ijara.dto.request.LoginRequest;
import com.example.ijara.dto.request.RegisterRequest;
import com.example.ijara.dto.request.TelegramLoginRequest;
import com.example.ijara.dto.response.LoginResponse;
import com.example.ijara.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autentifikatsiya", description = "Telegram WebApp va Admin panel uchun kirish")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/telegram-login")
    @Operation(
        summary = "Telegram orqali kirish",
        description = """
            Telegram Login Widget orqali kelgan ma'lumotlar (chatId, hash, authDate va boshqalar).
            Hash avtomatik tekshiriladi → yangi foydalanuvchi bo‘lsa ro‘yxatdan o‘tkaziladi.
            Muvaffaqiyatli bo‘lsa — JWT token qaytadi.
            """
    )
    public ResponseEntity<ApiResponse<LoginResponse>> telegramLogin(
            @Valid @RequestBody TelegramLoginRequest request
    ) {
        return ResponseEntity.ok(authService.telegramLogin(request));
    }

    @PostMapping("/register")
    @Operation(
        summary = "Foydalanuvchini email orqali ro'yxatdan o'tkazish",
        description = "Email va parol orqali yangi foydalanuvchini ro'yxatdan o'tkazadi."
    )
    public ResponseEntity<ApiResponse<String>> register(
        @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Email orqali tizimga kirish"
    )
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-email")
    @Operation(
        summary = "Emailni tasdiqlash"
    )
    public ResponseEntity<ApiResponse<LoginResponse>> verifyEmail(
        @RequestParam Integer code
    ){
        return ResponseEntity.ok(authService.verifyEmail(code));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Admin panelga kirish",
        description = "Faqat ADMIN roli bo‘lgan foydalanuvchilar uchun. Username + parol orqali kiriladi."
    )
    public ResponseEntity<ApiResponse<LoginResponse>> adminLogin(
            @Valid @RequestBody AdminLoginRequest request
    ) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @PatchMapping("/reset-password")
    @Operation(summary = "Parolni qayta o'rnatish")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String email){
        return ResponseEntity.ok(authService.resetPassword(email));
    }

    @PatchMapping("/check-code")
    @Operation(summary = "Kodni tekshirish")
    public ResponseEntity<ApiResponse<String>> checkCode(@RequestParam Integer code){
        return ResponseEntity.ok(authService.checkCode(code));
    }

    @PatchMapping("/set-password")
    @Operation(summary = "Parolni o'rnatish")
    public ResponseEntity<ApiResponse<String>> setPassword(
            @RequestParam String email,
            @RequestParam String newPassword
    ){
        return ResponseEntity.ok(authService.setPassword(newPassword,email));
    }
}