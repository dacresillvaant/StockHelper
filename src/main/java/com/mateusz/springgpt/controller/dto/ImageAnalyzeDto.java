package com.mateusz.springgpt.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ImageAnalyzeDto {
    private Long imageId;
    private BigDecimal ratio;
}
