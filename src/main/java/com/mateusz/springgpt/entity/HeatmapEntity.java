package com.mateusz.springgpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "heatmap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeatmapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdDate;

    private String source;

    @Column(columnDefinition = "TEXT")
    private String base64;

    private double ratio;

    private String modelVersion;
}