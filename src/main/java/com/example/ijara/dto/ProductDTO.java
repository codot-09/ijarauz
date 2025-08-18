package com.example.ijara.dto;

import com.example.ijara.dto.request.ReqProductPrice;
import com.example.ijara.dto.response.ResFeedback;
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
public class ProductDTO {

    private UUID id;

    private String name;

    private String description;

    private double rating;

    private String productType;

    private String productCondition;

    private double lat;

    private double lng;

    private int count;

    private List<String> imgUrls;

    private List<ReqProductPrice> reqProductPrices;

    private List<ResFeedback> feedbackList;
}
