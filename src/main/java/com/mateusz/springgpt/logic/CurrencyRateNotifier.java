package com.mateusz.springgpt.logic;

import com.mateusz.springgpt.controller.dto.CurrencyRateResponse;
import com.mateusz.springgpt.entity.CurrencyRateEntity;
import com.mateusz.springgpt.repository.CurrencyRateRepository;
import com.mateusz.springgpt.service.TwelveDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CurrencyRateNotifier {

    private final TwelveDataService twelveDataService;
    private final CurrencyRateRepository currencyRateRepository;

    @Autowired
    public CurrencyRateNotifier(TwelveDataService twelveDataService, CurrencyRateRepository currencyRateRepository) {
        this.twelveDataService = twelveDataService;
        this.currencyRateRepository = currencyRateRepository;
    }

    private void processCurrencyRate(String symbol) {
        CurrencyRateResponse currencyRateResponse = twelveDataService
                .getExchangeRate(symbol)
                .map(ResponseEntity::getBody)
                .block();

        CurrencyRateEntity currencyRateEntity = CurrencyRateEntity.builder()
                .createdDate(LocalDateTime.now())
                .ratioDate(LocalDateTime.now())
                .symbol(currencyRateResponse.getSymbol())
                .rate(currencyRateResponse.getRate())
                .build();

        currencyRateRepository.save(currencyRateEntity);
    }

    @Scheduled(cron = "${scheduler.usdpln}")
    public void processUsdPln() {
        processCurrencyRate("USD/PLN");
    }
}
