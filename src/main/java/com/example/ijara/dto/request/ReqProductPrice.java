package com.example.ijara.dto.request;

import com.example.ijara.entity.enums.ProductPriceType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqProductPrice {
    private double price;
    private String productPriceType;
}
