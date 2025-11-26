package com.example.ijara.dto.response;

import com.example.ijara.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;

    private String telegramChatId;

    private String identifier;

    private String firstName;

    private String lastName;

    private UserRole role;

    private boolean active;

    private LocalDateTime createdAt;
}
