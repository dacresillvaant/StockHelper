package com.mateusz.stockassistant.tools.mail;

import com.mateusz.stockassistant.controller.trend.GeoScope;
import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooTruncatedChartResponseDto;
import com.mateusz.stockassistant.entity.OwnedStockEntity;
import com.mateusz.stockassistant.logic.TrendNotifier;
import com.mateusz.stockassistant.service.yahoofinance.frontend.MailTemplateData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailTemplateFactory {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    private static final String ALERT = "ALERT - ";
    private static final String NOTIFICATION = "NOTIFICATION - ";

    public static MailTemplate currencyRateTemplate(YahooTruncatedChartResponseDto currencyRateResponse, String rateChangeDayBefore,
                                                    String rateChangeWeekBefore, String rateChangeMonthBefore) {
        String formattedTimestamp = DATE_FORMATTER.format(currencyRateResponse.getMeta().getDateOfPrice());

        String mailSubject = String.format(NOTIFICATION + "Currency rate report for: %s %s", currencyRateResponse.getMeta().getLongName(), formattedTimestamp);
        String mailBody = String.format("""
                        Currency rate of %s is %s - %s
                        Change 1D is: %s%%
                        Change 7D is: %s%%
                        Change 30D is: %s%%
                        """, currencyRateResponse.getMeta().getLongName(), currencyRateResponse.getMeta().getLastPrice(), formattedTimestamp,
                rateChangeDayBefore, rateChangeWeekBefore, rateChangeMonthBefore);

        return new MailTemplate(mailSubject, mailBody);
    }

    public static MailTemplate lowPriceAlertTemplate(String symbol, int percentChange, YahooTruncatedChartResponseDto quote,
                                                     BigDecimal lastClose, BigDecimal yearHigh, BigDecimal alertThreshold) {
        String symbolFullName = quote.getMeta().getLongName();
        String mailSubject = ALERT + "Watched stock " + symbolFullName + " fallen below threshold price";
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

        String mailSubject = ALERT + "Owned stock " + symbolFullName + " price significantly changed!";
        String mailBody = String.format("""
                Purchase price of %s %s is: %s %s
                Latest close is: %s
                Change: %s%%, threshold was set to -> %s%%
                """, symbol, symbolFullName, purchasePrice, symbolCurrency, lastClosePrice, priceChange, percentChangeThreshold);

        return new MailTemplate(mailSubject, mailBody);
    }

    public static MailTemplate trendTemplate(GeoScope geoScope, List<TrendNotifier.TrendTableRow> trendsTableData) {
        String mailSubject = NOTIFICATION + "Top Google trends in " + geoScope.getFullName();

        String rowDataTemplate = "%d. Trend: %s, Search volume: %s Started: %s%n";
        String mailBody = trendsTableData.stream()
                .map(row -> String.format(rowDataTemplate, trendsTableData.indexOf(row) + 1, row.column1(), row.column2(), row.column3()))
                .reduce(geoScope.getFullName().concat(" past week trends - ").concat(LocalDateTime.now().format(DATE_FORMATTER)).concat("\n"), String::concat);

        return new MailTemplate(mailSubject, mailBody);
    }

    public static MailTemplate analystInsightsTemplate(List<MailTemplateData> mailTemplateDataList) {
        String mailSubject = NOTIFICATION + "Analyst insights for " + mailTemplateDataList.get(0).stockType() + " stocks";

        String rowDataTemplate = """
                Analyst insights for: %s, stock type: %s
                Current price: %s
                Analyst predicted low price: %s
                
                """;

        String mailBody = mailTemplateDataList.stream()
                .map(data -> String.format(rowDataTemplate, data.ticker(), data.stockType(), data.currentPrice(), data.lowPrice()))
                .reduce("Analyst insights report - ".concat(LocalDateTime.now().format(DATE_FORMATTER)).concat("\n \n"), String::concat);

        return new MailTemplate(mailSubject, mailBody);
    }
}