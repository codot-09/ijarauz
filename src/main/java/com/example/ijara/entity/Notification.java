package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification extends BaseEntity {


    private String title;

    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private boolean read;

}
