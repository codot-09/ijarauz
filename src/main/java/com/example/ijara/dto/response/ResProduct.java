package com.example.ijara.dto.response;

import com.example.ijara.dto.request.ReqProductPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResProduct {

    private UUID id;
    private String name;
    private String description;
    private String productType;
    private String productCondition;
    private Double rating;
    private Double lat;
    private Double lng;
    private Double price;
    private List<String> imgUrls;
}
