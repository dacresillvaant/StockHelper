package com.mateusz.springgpt.controller.twelvedata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CurrencyRateInternalDto {
    private String symbol;
    private BigDecimal rate;
    private LocalDateTime ratioDate;
}