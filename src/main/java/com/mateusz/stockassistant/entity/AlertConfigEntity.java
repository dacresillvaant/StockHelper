package com.mateusz.stockassistant.entity;

import com.mateusz.stockassistant.controller.alertconfig.AlertType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_config", uniqueConstraints = {@UniqueConstraint(columnNames = {"ticker", "alert_type"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private String ticker;

    private int percentChangeThreshold;

    @Enumerated(EnumType.STRING)
    private AlertType alertType;
}