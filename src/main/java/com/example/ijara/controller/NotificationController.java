package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.IdList;
import com.example.ijara.dto.NotificationDTO;
import com.example.ijara.dto.response.ResNotification;
import com.example.ijara.entity.User;
import com.example.ijara.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Bildirishnomalar", description = "Foydalanuvchilarga xabar yuborish va boshqarish")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/broadcast")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Barcha foydalanuvchilarga xabar yuborish")
    public ResponseEntity<ApiResponse<String>> broadcast(
            @Valid @RequestBody ResNotification request
    ) {
        return ResponseEntity.ok(notificationService.adminSendNotificationToAll(request));
    }

    @PostMapping("/to/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bitta foydalanuvchiga shaxsiy xabar yuborish")
    public ResponseEntity<ApiResponse<String>> sendToUser(
            @Parameter(description = "Xabar oluvchi foydalanuvchi ID") 
            @PathVariable UUID userId,
            @Valid @RequestBody ResNotification request
    ) {
        return ResponseEntity.ok(notificationService.createNotification(userId, request));
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "O‘zimga kelgan barcha bildirishnomalarni ko‘rish")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getMyNotifications(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(notificationService.getMyNotifications(user));
    }

    @GetMapping("/unread-count")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "O‘qilmagan bildirishnomalar soni")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationCount(user));
    }

    @PatchMapping("/mark-as-read")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Bir nechta bildirishnomani o‘qilgan deb belgilash")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @Valid @RequestBody IdList idList
    ) {
        return ResponseEntity.ok(notificationService.markAsRead(idList));
    }

    @DeleteMapping("/{notificationId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bildirishnomani o‘chirish (faqat ADMIN)")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @Parameter(description = "O‘chiriladigan bildirishnoma ID") 
            @PathVariable UUID notificationId,

            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId,user));
    }
}