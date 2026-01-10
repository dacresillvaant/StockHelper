package com.mateusz.springgpt.controller.twelvedata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mateusz.springgpt.controller.twelvedata.dto.model.FiftyTwoWeek;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuoteExternalDto {
    private String symbol;
    private String name;
    private String exchange;
    @JsonProperty("mic_code")
    private String micCode;
    private String currency;
    private String datetime;
    private long timestamp;
    @JsonProperty("last_quote_at")
    private long lastQuoteAt;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    @JsonProperty("previous_close")
    private String previousClose;
    private String change;
    @JsonProperty("percent_change")
    private String percentChange;
    @JsonProperty("average_volume")
    private String averageVolume;
    @JsonProperty("is_market_open")
    private boolean isMarketOpen;
    @JsonProperty("fifty_two_week")
    private FiftyTwoWeek fiftyTwoWeek;
}