package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.UpdateProfileRequest;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Foydalanuvchilar", description = "Profil boshqaruvi va admin funksiyalari")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Joriy foydalanuvchi profilini ko‘rish")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PatchMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Profilni yangilash")
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        return ResponseEntity.ok(userService.updateProfile(user, req));
    }

    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Foydalanuvchilarni qidirish (faqat ADMIN)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> search(
            @Parameter(description = "Ism, familiya yoki username bo‘yicha qidiruv") 
            @RequestParam(required = false) String query,

            @Parameter(description = "Rol bo‘yicha filter") 
            @RequestParam(required = false) UserRole role,

            @Parameter(description = "Sahifa raqami") 
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Sahifa hajmi") 
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(query, role, pageable));
    }

    @GetMapping("/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Foydalanuvchini ID bo‘yicha olish")
    public ResponseEntity<ApiResponse<UserResponse>> getById(
            @Parameter(description = "Foydalanuvchi ID") 
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PatchMapping("/{userId}/block")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Foydalanuvchini bloklash (faqat ADMIN)")
    public ResponseEntity<ApiResponse<String>> blockUser(
            @Parameter(description = "Bloklanadigan foydalanuvchi ID") 
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.blockUser(userId));
    }

    @PatchMapping("/{userId}/unblock")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Foydalanuvchini blokdan chiqarish (faqat ADMIN)")
    public ResponseEntity<ApiResponse<String>> unblockUser(
            @Parameter(description = "Blokdan chiqariladigan foydalanuvchi ID") 
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(userService.unblockUser(userId));
    }

    @PatchMapping("/{userId}/role")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Foydalanuvchi rolini o‘zgartirish (faqat ADMIN)")
    public ResponseEntity<ApiResponse<String>> changeRole(
            @Parameter(description = "Rol o‘zgartiriladigan foydalanuvchi ID") 
            @PathVariable UUID userId,

            @Parameter(description = "Yangi rol") 
            @RequestParam UserRole role
    ) {
        return ResponseEntity.ok(userService.changeRole(userId, role));
    }
}