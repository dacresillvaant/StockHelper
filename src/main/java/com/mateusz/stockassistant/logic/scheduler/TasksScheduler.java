package com.mateusz.stockassistant.logic.scheduler;

import com.mateusz.stockassistant.controller.trend.GeoScope;
import com.mateusz.stockassistant.controller.yahoofinance.SymbolMapper;
import com.mateusz.stockassistant.logic.CurrencyRateNotifier;
import com.mateusz.stockassistant.logic.DynamicAlert;
import com.mateusz.stockassistant.logic.HeatMapScrapper;
import com.mateusz.stockassistant.logic.TrendNotifier;
import com.mateusz.stockassistant.service.yahoofinance.frontend.StockType;
import com.mateusz.stockassistant.service.yahoofinance.frontend.YahooFinanceAnalystInsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.mateusz.stockassistant.controller.yahoofinance.SymbolMapper.USDPLN;

@Component
public class TasksScheduler {

    private final HeatMapScrapper heatMapScrapper;
    private final DynamicAlert dynamicAlert;
    private final CurrencyRateNotifier currencyRateNotifier;
    private final TrendNotifier trendNotifier;
    private final YahooFinanceAnalystInsightsService yahooFinanceAnalystInsightsService;

    @Autowired
    public TasksScheduler(HeatMapScrapper heatMapScrapper, DynamicAlert dynamicAlert,
                          CurrencyRateNotifier currencyRateNotifier, TrendNotifier trendNotifier,
                          YahooFinanceAnalystInsightsService yahooFinanceAnalystInsightsService) {
        this.heatMapScrapper = heatMapScrapper;
        this.dynamicAlert = dynamicAlert;
        this.currencyRateNotifier = currencyRateNotifier;
        this.trendNotifier = trendNotifier;
        this.yahooFinanceAnalystInsightsService = yahooFinanceAnalystInsightsService;
    }

    @Scheduled(cron = "${scheduler.heatmap.cron}")
    public void scheduleScrapHeatMap() {
        heatMapScrapper.scrapHeatMap();
    }

    @Scheduled(cron = "${scheduler.low-price-alert}")
    public void scheduledLowPriceAlert() {
        dynamicAlert.lowPriceAlert();
    }

    @Scheduled(cron = "${scheduler.owned-stock-alert}")
    public void scheduleOwnedStockPriceAlert() {
        dynamicAlert.ownedStockPriceAlert(30);
    }

    @Scheduled(cron = "${scheduler.process-currencies}")
    public void scheduledProcessCurrencyRate() {
        List<SymbolMapper> currencies = List.of(USDPLN);
        currencies.forEach(currency -> currencyRateNotifier.processCurrencyRate(currency.getYahooValue()));
    }

    @Scheduled(cron = "${scheduler.trend-checker}")
    public void scheduleTrendCheck() {
        for (GeoScope geoScope : GeoScope.values()) {
            trendNotifier.checkTrends(geoScope);
        }
    }

    @Scheduled(cron = "${scheduler.analyst-insights}")
    public void scheduleAnalystInsightsCheck() {
        Arrays.stream(StockType.values()).toList().forEach(yahooFinanceAnalystInsightsService::checkAnalystInsightsOfStocks);
    }
}