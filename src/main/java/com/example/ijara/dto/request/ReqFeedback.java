package com.example.ijara.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqFeedback {
    @NotBlank
    private String feedback;

    @Max(5)
    @Min(1)
    private double rating;

    @NotBlank
    private UUID productId;

}
