package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import com.example.ijara.entity.enums.ProductCondition;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    private Category category;

    @ManyToOne
    private User owner;

    private double lat;
    private double lng;
    private int count;

    @Enumerated(EnumType.STRING)
    private ProductCondition productCondition;

    private boolean active;
}
