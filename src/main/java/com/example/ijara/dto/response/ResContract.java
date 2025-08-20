package com.example.ijara.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResContract {

    private UUID contractId;

    private String productName;

    private UUID productId;

    private String lesseeName;

    private UUID lesseeId;

    @Schema(description = "Boshlanish sanasi", example = "2025-08-19 10:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @Schema(description = "Boshlanish sanasi", example = "2025-08-19 10:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    @Schema(hidden = true)
    private ResProductDuration duration;

    private double price;

    private String contractStatus;
}
