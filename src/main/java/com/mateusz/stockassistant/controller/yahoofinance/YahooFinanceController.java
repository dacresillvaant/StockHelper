package com.mateusz.stockassistant.controller.yahoofinance;

import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooDetailedChartResponseDto;
import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooTruncatedChartResponseDto;
import com.mateusz.stockassistant.service.YahooFinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/yahoofinance")
@Slf4j
public class YahooFinanceController {

    private final YahooFinanceService yahooFinanceService;

    @Autowired
    public YahooFinanceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/data/")
    public String getData(@RequestParam String symbol, @RequestParam(required = false) String range,  @RequestParam(required = false) String interval) {

        if (range == null && interval == null) {
            return yahooFinanceService.getData(symbol);
        } else {
            return yahooFinanceService.getData(symbol, range, interval);
        }
    }

    @GetMapping("/data/simplified/")
    public YahooTruncatedChartResponseDto getSimplifiedData(@RequestParam String symbol) {
        String resolvedSymbol;

        try {
            resolvedSymbol = SymbolMapper.valueOf(symbol).getYahooValue();
        } catch (IllegalArgumentException e) {
            log.info("Mapping for {} not found, passing as a raw value.", symbol);
            resolvedSymbol = symbol;
        }

        return yahooFinanceService.getSimplifiedData(resolvedSymbol);
    }

    @GetMapping("/data/detailed/")
    public YahooDetailedChartResponseDto getDetailedData(@RequestParam String symbol, @RequestParam String range, @RequestParam String interval) {
        String resolvedSymbol;

        try {
            resolvedSymbol = SymbolMapper.valueOf(symbol).getYahooValue();
        } catch (IllegalArgumentException e) {
            log.info("Mapping for {} not found, passing as a raw value.", symbol);
            resolvedSymbol = symbol;
        }

        return yahooFinanceService.getDetailedData(resolvedSymbol, range, interval);
    }
}