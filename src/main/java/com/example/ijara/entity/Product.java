package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import com.example.ijara.entity.enums.ProductCondition;
import com.example.ijara.entity.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cache.annotation.EnableCaching;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product extends BaseEntity {

    private String name;

    private String description;

    @ElementCollection
    private List<String> imgUrls;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @ManyToOne
    private User owner;

    private double lat;
    private double lng;

    @Enumerated(EnumType.STRING)
    private ProductCondition productCondition;
}
