package com.example.ijara.entity;

import com.example.ijara.entity.base.BaseEntity;
import com.example.ijara.entity.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Contract extends BaseEntity {

    @ManyToOne
    private User owner;

    @ManyToOne
    private User lessee;  //ijara oluvchi

    @ManyToOne
    private Product product;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private double price;

    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus;


    private boolean active;
}
