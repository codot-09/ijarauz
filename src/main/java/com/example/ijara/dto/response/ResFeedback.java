package com.example.ijara.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResFeedback {
    private String feedback;
    private double rating;
    private UUID userId;
    private String userName;
}
