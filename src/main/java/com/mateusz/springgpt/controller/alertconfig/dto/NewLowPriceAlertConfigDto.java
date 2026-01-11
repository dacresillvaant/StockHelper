package com.mateusz.springgpt.controller.alertconfig.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NewLowPriceAlertConfigDto {

    private String ticker;
    private int percentChangeThreshold;
}