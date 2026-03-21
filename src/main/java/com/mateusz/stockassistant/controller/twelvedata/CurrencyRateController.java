package com.mateusz.stockassistant.controller.twelvedata;

import com.mateusz.stockassistant.controller.twelvedata.dto.CurrencyRateInternalDto;
import com.mateusz.stockassistant.service.CurrencyRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/currencyrate/")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @Autowired
    public CurrencyRateController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @GetMapping("/exchange_rate_database/")
    public CurrencyRateInternalDto getExchangeRateFromDatabase(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime ratioDate,
                                                               @RequestParam String symbol) {
        return currencyRateService.getExchangeRateFromDatabase(ratioDate, symbol);
    }
}