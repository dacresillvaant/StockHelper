package com.mateusz.springgpt.controller.nbp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NbpCurrencyRateDto {

    private String table;
    private String currency;
    private String code;
    private List<Rate> rates;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rate {
        private String no;
        private LocalDate effectiveDate;
        private BigDecimal mid;
    }
}