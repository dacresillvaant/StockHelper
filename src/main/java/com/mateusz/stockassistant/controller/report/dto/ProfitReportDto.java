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
import java.math.RoundingMode;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ProfitReportDto {

    private static class PriceWithCurrencySerializer extends JsonSerializer<PriceWithCurrency> {
        @Override
        public void serialize(PriceWithCurrency value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.price() + " " + value.currency());
        }
    }

    private static class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeString(value.setScale(2, RoundingMode.HALF_UP) + "%");
        }
    }

    @JsonSerialize(using = PriceWithCurrencySerializer.class)
    public record PriceWithCurrency(BigDecimal price, String currency) {
    }

    @Builder
    public record SingleShare(
            PriceWithCurrency purchasePrice,
            PriceWithCurrency purchasePriceConverted,
            PriceWithCurrency todayPrice,
            PriceWithCurrency todayPriceConverted,
            PriceWithCurrency diff,
            PriceWithCurrency diffConverted
    ) {
    }

    @Builder
    public record TotalShares(
            int amount,
            PriceWithCurrency purchasePrice,
            PriceWithCurrency purchasePriceConverted,
            PriceWithCurrency todayPrice,
            PriceWithCurrency todayPriceConverted,
            PriceWithCurrency diff,
            PriceWithCurrency diffConverted,
            @JsonSerialize(using = BigDecimalSerializer.class) BigDecimal diffPercentage
    ) {
    }

    private String name;
    private String ticker;
    private LocalDate purchaseDate;
    BigDecimal purchaseCurrencyRateToPLN;
    BigDecimal todayCurrencyRateToPLN;
    SingleShare singleShare;
    TotalShares totalShares;
}