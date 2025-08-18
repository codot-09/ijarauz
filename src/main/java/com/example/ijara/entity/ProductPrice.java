package com.example.ijara.entity;

import com.example.ijara.entity.enums.ProductPriceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Product product;

    private double price;

    @Enumerated(EnumType.STRING)
    private ProductPriceType productPriceType;

    private boolean active;
}
