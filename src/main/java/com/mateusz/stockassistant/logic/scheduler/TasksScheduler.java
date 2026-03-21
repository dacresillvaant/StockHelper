package com.mateusz.stockassistant.logic.scheduler;

import com.mateusz.stockassistant.controller.yahoofinance.SymbolMapper;
import com.mateusz.stockassistant.logic.CurrencyRateNotifier;
import com.mateusz.stockassistant.logic.DynamicAlert;
import com.mateusz.stockassistant.logic.HeatMapScrapper;
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

    @Autowired
    public TasksScheduler(HeatMapScrapper heatMapScrapper, DynamicAlert dynamicAlert, CurrencyRateNotifier currencyRateNotifier) {
        this.heatMapScrapper = heatMapScrapper;
        this.dynamicAlert = dynamicAlert;
        this.currencyRateNotifier = currencyRateNotifier;
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
}