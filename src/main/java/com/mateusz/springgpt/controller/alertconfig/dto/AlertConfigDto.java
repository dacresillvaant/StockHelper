package com.mateusz.springgpt.controller.alertconfig.dto;

import com.mateusz.springgpt.controller.alertconfig.AlertType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlertConfigDto {

    private String ticker;
    private int percentChangeThreshold;
    private AlertType alertType;
}