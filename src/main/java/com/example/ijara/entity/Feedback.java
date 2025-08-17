package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Feedback extends BaseEntity {

    private String feedback;

    @Min(1)
    @Max(5)
    private double rating; // 1-5 gacha

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user; //feedback bildirgan odam
}
