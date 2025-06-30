package com.mateusz.springgpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "owned_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnedStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private LocalDateTime boughtDate;

    @Column(unique = true)
    private String ticker;

    private String name;

    private int position;

    private BigDecimal purchasePrice;

    private String currency;
}