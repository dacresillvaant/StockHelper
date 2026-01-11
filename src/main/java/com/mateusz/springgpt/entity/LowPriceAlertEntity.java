package com.mateusz.springgpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "low_price_alert_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowPriceAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    @Column(unique = true)
    private String ticker;

    private int percentChangeThreshold;
}