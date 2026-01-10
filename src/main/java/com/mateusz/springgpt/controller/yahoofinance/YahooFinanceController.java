package com.mateusz.springgpt.controller.yahoofinance;

import com.mateusz.springgpt.service.YahooFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/yahoofinance")
public class YahooFinanceController {

    private final YahooFinanceService yahooFinanceService;

    @Autowired
    public YahooFinanceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/data/")
    public Mono<ResponseEntity<String>> getData(
            @RequestParam String symbol,
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String interval) {
        if (range == null && interval == null) {
            return yahooFinanceService.getData(symbol);
        } else {
            return yahooFinanceService.getData(symbol, range, interval);
        }
    }
}