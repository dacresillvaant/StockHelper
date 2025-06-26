package com.mateusz.springgpt.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private String ticker;

    private String name;

    private int position;

    private int purchasePrice;

    private String currency;
}