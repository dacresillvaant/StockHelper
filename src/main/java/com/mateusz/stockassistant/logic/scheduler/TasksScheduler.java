package com.mateusz.stockassistant.logic.scheduler;

import com.mateusz.stockassistant.controller.trend.GeoScope;
import com.mateusz.stockassistant.controller.yahoofinance.SymbolMapper;
import com.mateusz.stockassistant.logic.CurrencyRateNotifier;
import com.mateusz.stockassistant.logic.DynamicAlert;
import com.mateusz.stockassistant.logic.HeatMapScrapper;
import com.mateusz.stockassistant.logic.TrendNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.mateusz.stockassistant.controller.yahoofinance.SymbolMapper.USDPLN;

@Component
public class TasksScheduler {

    private final HeatMapScrapper heatMapScrapper;
    private final DynamicAlert dynamicAlert;
    private final CurrencyRateNotifier currencyRateNotifier;
    private final TrendNotifier trendNotifier;

    @Autowired
    public TasksScheduler(HeatMapScrapper heatMapScrapper, DynamicAlert dynamicAlert, CurrencyRateNotifier currencyRateNotifier, TrendNotifier trendNotifier) {
        this.heatMapScrapper = heatMapScrapper;
        this.dynamicAlert = dynamicAlert;
        this.currencyRateNotifier = currencyRateNotifier;
        this.trendNotifier = trendNotifier;
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
}