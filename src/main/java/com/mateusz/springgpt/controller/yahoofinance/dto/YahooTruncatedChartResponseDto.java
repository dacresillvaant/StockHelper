package com.mateusz.springgpt.controller.yahoofinance.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YahooTruncatedChartResponseDto {

    private Chart chart;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Chart {
        private List<Result> result;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private Meta meta;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private String symbol;
        private String shortName;
        private String longName;
        private String currency;
        private String exchangeName;
        private String fullExchangeName;
        private String instrumentType;

        @JsonAlias("regularMarketPrice")
        @JsonProperty("lastPrice")
        private BigDecimal lastPrice;

        private BigDecimal fiftyTwoWeekHigh;
        private BigDecimal fiftyTwoWeekLow;
    }
}