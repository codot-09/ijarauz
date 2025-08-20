package com.example.ijara.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public record NotificationDTO(
        UUID id,

        String title,

        String content,

        UUID userId,

        boolean read,

        LocalDateTime createdAt
) {
}
