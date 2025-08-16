package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.response.UserResponse;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.UserRole;
import com.example.ijara.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Foydalanuvchi API",description = "Foydalanuvchilarlarni boshqarish uchun endpointlar")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "O'z profilini ko'rish",security = @SecurityRequirement(name ="bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Foydalanuvchilarni qidirish",
            description = "Null bo'lmagan fieldlar uchun qidiradi, Agar hammasi null berilsa barcha userlar chiqadi,",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Page<UserResponse>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false)UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(userService.search(name,role,pageable));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "ID orqali foydalanuvchini olish",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID userId){
        return ResponseEntity.ok(userService.getById(userId));
    }
}
