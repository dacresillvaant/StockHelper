package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.controller.dto.CurrencyRateResponse;
import com.mateusz.springgpt.service.TwelveDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/twelvedata/")
public class TwelveDataController {

    private final TwelveDataService twelveDataService;

    @Autowired
    public TwelveDataController(TwelveDataService twelveDataService) {
        this.twelveDataService = twelveDataService;
    }

    @GetMapping("/api_usage/")
    public Mono<ResponseEntity<String>> getUsage() {
        return twelveDataService.getUsage();
    }

    @GetMapping("/exchange_rate/")
    public Mono<ResponseEntity<CurrencyRateResponse>> getExchangeRate(@RequestParam String symbol) {
        return twelveDataService.getExchangeRate(symbol);
    }
}