package com.mateusz.stockassistant.controller.report.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ProfitReportDto {

    private static class PriceWithCurrencySerializer extends JsonSerializer<PriceWithCurrency> {
        @Override
        public void serialize(PriceWithCurrency value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.price() + " " +  value.currency());
        }
    }

    @JsonSerialize(using = PriceWithCurrencySerializer.class)
    public record PriceWithCurrency(BigDecimal price, String currency) {}

    private String name;
    private String ticker;
    private LocalDate purchaseDate;
    BigDecimal purchaseCurrencyRateToPLN;
    BigDecimal todayCurrencyRateToPLN;
    PriceWithCurrency purchasePrice;
    PriceWithCurrency purchasePriceConverted;
    PriceWithCurrency todayPrice;
    PriceWithCurrency todayPriceConverted;
    PriceWithCurrency diff;
    PriceWithCurrency diffConverted;
}