package com.example.ijara.dto.request;

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
public class ReqProduct {

    @NotBlank
    private String name;

    private String description;

    @NotEmpty
    private List<String> imgUrls;

    @NotBlank
    private double lat;

    @NotBlank
    private double lng;

    private int count;

    @NotBlank
    private List<ReqProductPrice> reqProductPrices;
}
