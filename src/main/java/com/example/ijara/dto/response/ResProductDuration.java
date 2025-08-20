package com.example.ijara.dto.response;

import com.example.ijara.entity.enums.ProductPriceType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResProductDuration {
    private long duration;
    private ProductPriceType productPriceType;
}
