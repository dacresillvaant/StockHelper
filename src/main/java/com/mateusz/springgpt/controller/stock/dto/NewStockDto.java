package com.mateusz.springgpt.controller.stock.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NewStockDto {

    private LocalDateTime boughtDate;
    private String ticker;
    private String name;
    private int position;
    private BigDecimal purchasePrice;
    private String currency;
}