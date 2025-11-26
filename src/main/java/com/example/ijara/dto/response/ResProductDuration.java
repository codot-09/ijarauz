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

    public static ResProductDuration of(long duration, ProductPriceType type) {
        ResProductDuration res = new ResProductDuration();
        res.setDuration(duration);
        res.setProductPriceType(type);
        return res;
    }
}
