package com.mateusz.springgpt.controller.twelvedata.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FiftyTwoWeek {
    private String low;
    private String high;
    @JsonProperty("low_change")
    private String lowChange;
    @JsonProperty("high_change")
    private String highChange;
    @JsonProperty("low_change_percent")
    private String lowChangePercent;
    @JsonProperty("high_change_percent")
    private String highChangePercent;
    private String range;
}