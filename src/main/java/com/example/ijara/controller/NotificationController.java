package com.example.ijara.controller;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.IdList;
import com.example.ijara.dto.response.ResNotification;
import com.example.ijara.entity.User;
import com.example.ijara.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Notification Controller", description = "Bildirishnomalar bilan ishlovchi endpointlar")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Admin barcha userlarga notification yuborish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> sendNotification(@RequestBody ResNotification resNotification) {
        return ResponseEntity.ok(notificationService.adminSendNotificationAll(resNotification));
    }


    @PostMapping("/{studentId}")
    @Operation(summary = "Admin bitta userga notification yuborish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> sendNotificationOneStudent(@PathVariable UUID studentId,
                                                                     @RequestBody ResNotification resNotification) {
        return ResponseEntity.ok(notificationService.createNotification(studentId, resNotification));
    }



    @GetMapping("/my")
    @Operation(summary = "Barcha uziga kelgan notificationlarni kurish")
    public ResponseEntity<ApiResponse<?>> getMyNotifications(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(notificationService.getMyNotifications(student));
    }


    @PutMapping("/isRead")
    @Operation(summary = "O'qilmagan notificationlarni uqilgan qiladi")
    public ResponseEntity<ApiResponse<?>> isRead(@RequestBody IdList idList) {
        return ResponseEntity.ok(notificationService.readNotification(idList));
    }


    @GetMapping("/count")
    @Operation(summary = "Barcha uziga kelgan uqilmagan notificationlar soni")
    public ResponseEntity<ApiResponse<?>> getNotificationCount(@AuthenticationPrincipal User student) {
        return ResponseEntity.ok(notificationService.getUnReadNotificationCount(student));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Admin notificationni uchirishi uchun")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteNotification(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.deleteNotification(id));
    }
}
