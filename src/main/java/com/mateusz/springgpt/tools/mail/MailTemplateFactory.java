package com.mateusz.springgpt.tools.mail;

import com.mateusz.springgpt.controller.twelvedata.dto.CurrencyRateExternalDto;
import com.mateusz.springgpt.controller.twelvedata.dto.QuoteExternalDto;
import com.mateusz.springgpt.entity.OwnedStockEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MailTemplateFactory {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    public static MailTemplate currencyRateTemplate(CurrencyRateExternalDto currencyRateResponse, String rateChangeDayBefore,
                                                    String rateChangeWeekBefore, String rateChangeMonthBefore) {
        String formattedTimestamp = DATE_FORMATTER.format(Instant.ofEpochSecond(currencyRateResponse.getTimestamp()));

        String mailSubject = String.format("Currency rate report for: %s %s", currencyRateResponse.getSymbol(), formattedTimestamp);
        String mailBody = String.format("""
                Currency rate of %s is %s - %s
                Change 1D is: %s%%
                Change 7D is: %s%%
                Change 30D is: %s%%
                """, currencyRateResponse.getSymbol(), currencyRateResponse.getRate(), formattedTimestamp,
                rateChangeDayBefore, rateChangeWeekBefore, rateChangeMonthBefore);

        return new MailTemplate(mailSubject, mailBody);
    }

    public static MailTemplate lowPriceAlertTemplate(String symbol, int percentChange, QuoteExternalDto quote,
                                                     BigDecimal lastClose, BigDecimal yearHigh, BigDecimal alertThreshold) {
        String symbolFullName = quote.getName();
        String mailSubject = "ALERT - ".concat(symbolFullName).concat(" fallen below threshold price");
        String mailBody = String.format("""
                Latest price of %s %s is: %s
                Year high is: %s
                Threshold was set to: %s%% -> %s
                """, symbol, symbolFullName, lastClose, yearHigh, percentChange, alertThreshold);

        return new MailTemplate(mailSubject, mailBody);
    }

    public static MailTemplate ownedStockPriceAlertTemplate(OwnedStockEntity ownedStock, BigDecimal purchasePrice, BigDecimal lastClosePrice,
                                                            BigDecimal priceChange, int percentChangeThreshold) {
        String symbol = ownedStock.getTicker();
        String symbolFullName = ownedStock.getName();
        String symbolCurrency = ownedStock.getCurrency();

        String mailSubject = "ALERT - ".concat(symbolFullName).concat(" price significantly changed!");
        String mailBody = String.format("""
                Purchase price of %s %s is: %s %s
                Latest close is: %s
                Change: %s%%, threshold was set to -> %s%%
                """, symbol, symbolFullName, purchasePrice, symbolCurrency, lastClosePrice, priceChange, percentChangeThreshold);

        return new MailTemplate(mailSubject, mailBody);
    }
}