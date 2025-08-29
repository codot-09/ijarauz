package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Category extends BaseEntity {
    private String name;
    private boolean deleted;
}
