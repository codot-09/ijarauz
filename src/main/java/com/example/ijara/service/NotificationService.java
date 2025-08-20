package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.IdList;
import com.example.ijara.dto.response.ResNotification;
import com.example.ijara.entity.User;

import java.util.UUID;

public interface NotificationService {
    ApiResponse<?> adminSendNotificationAll(ResNotification resNotification);
    ApiResponse<?> createNotification(UUID userId, ResNotification resNotification);
    ApiResponse<?> getMyNotifications(User user);
    ApiResponse<?> getUnReadNotificationCount(User user);
    ApiResponse<?> readNotification(IdList idList);
    ApiResponse<?> deleteNotification(UUID id);
}
