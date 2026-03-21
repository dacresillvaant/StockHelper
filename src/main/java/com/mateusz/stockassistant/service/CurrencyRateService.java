package com.mateusz.stockassistant.service;

import com.mateusz.stockassistant.controller.twelvedata.dto.CurrencyRateInternalDto;
import com.mateusz.stockassistant.repository.CurrencyRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class CurrencyRateService {

    private final CurrencyRateRepository currencyRateRepository;

    @Autowired
    public CurrencyRateService(com.mateusz.stockassistant.repository.CurrencyRateRepository currencyRateRepository) {
        this.currencyRateRepository = currencyRateRepository;
    }

    public CurrencyRateInternalDto getExchangeRateFromDatabase(LocalDateTime ratioDate, String symbol) {
        LocalDateTime start = ratioDate.minusMinutes(5);
        LocalDateTime end = ratioDate.plusMinutes(5);

        return (currencyRateRepository.findExchangeRateByRatioDateBetween(start, end, symbol))
                .orElseThrow(() -> new NoSuchElementException("Currency rate of " + symbol + " between " + start + " and " + end + " not found"));
    }
}