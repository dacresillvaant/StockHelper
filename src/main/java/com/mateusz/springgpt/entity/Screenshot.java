package com.mateusz.springgpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "screenshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screenshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    private String source;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String base64Image;
}