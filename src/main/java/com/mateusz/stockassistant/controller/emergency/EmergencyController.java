package com.mateusz.stockassistant.controller.emergency;

import com.mateusz.stockassistant.service.YahooFinanceAnalystInsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyController {

    private final YahooFinanceAnalystInsightsService yahooFinanceAnalystInsightsService;

    @Autowired
    public EmergencyController(YahooFinanceAnalystInsightsService yahooFinanceAnalystInsightsService) {
        this.yahooFinanceAnalystInsightsService = yahooFinanceAnalystInsightsService;
    }

    @GetMapping("/get/analystinsights")
    public void getDevelopedFunctionality3() {
        yahooFinanceAnalystInsightsService.checkAnalystInsightsOfStocks();
    }
}