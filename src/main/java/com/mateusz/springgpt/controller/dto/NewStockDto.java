package com.mateusz.springgpt.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NewStockDto {

    private LocalDateTime boughtDate;
    private String ticker;
    private String name;
    private int position;
    private int purchasePrice;
    private String currency;
}