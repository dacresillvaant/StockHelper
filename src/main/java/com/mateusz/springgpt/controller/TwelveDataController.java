package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.controller.dto.CurrencyRateExternalDto;
import com.mateusz.springgpt.controller.dto.CurrencyRateInternalDto;
import com.mateusz.springgpt.controller.dto.QuoteExternalDto;
import com.mateusz.springgpt.service.TwelveDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/twelvedata/")
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
    public Mono<ResponseEntity<CurrencyRateExternalDto>> getExchangeRate(@RequestParam String symbol) {
        return twelveDataService.getExchangeRate(symbol);
    }

    @GetMapping("/quote/")
    public Mono<ResponseEntity<QuoteExternalDto>> getQuote(@RequestParam String symbol) {
        return twelveDataService.getQuote(symbol);
    }

    @GetMapping("/exchange_rate_database/")
    public CurrencyRateInternalDto getExchangeRateFromDatabase(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime ratioDate,
                                                               @RequestParam String symbol) {
        return twelveDataService.getExchangeRateFromDatabase(ratioDate, symbol);
    }
}