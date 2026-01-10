package com.mateusz.springgpt.service;

import com.mateusz.springgpt.config.WebClientLoggingUtil;
import com.mateusz.springgpt.controller.twelvedata.dto.CurrencyRateExternalDto;
import com.mateusz.springgpt.controller.twelvedata.dto.CurrencyRateInternalDto;
import com.mateusz.springgpt.controller.twelvedata.dto.QuoteExternalDto;
import com.mateusz.springgpt.repository.CurrencyRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Free API key is limited to 8 credits/minute & 800/day
 */
@Slf4j
@Service
public class TwelveDataService {

    private final WebClient webClient;
    private final CurrencyRateRepository currencyRateRepository;

    @Autowired
    public TwelveDataService(@Value("${twelvedata.url}") String baseUrl,
                             @Value("${twelvedata.api-key}") String apiKey,
                             CurrencyRateRepository currencyRateRepository) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "apikey " + apiKey)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logRequest());
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logResponse());
                })
                .build();
        this.currencyRateRepository = currencyRateRepository;
    }

    public Mono<ResponseEntity<String>> getUsage() {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("api_usage")
                        .build())
                .retrieve().toEntity(String.class);
    }

    public Mono<ResponseEntity<CurrencyRateExternalDto>> getExchangeRate(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("exchange_rate")
                        .queryParam("symbol", symbol)
                        .build())
                .retrieve().toEntity(CurrencyRateExternalDto.class);
    }

    public Mono<ResponseEntity<QuoteExternalDto>> getQuote(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("quote")
                        .queryParam("symbol", symbol)
                        .build())
                .retrieve().toEntity(QuoteExternalDto.class);
    }

    public CurrencyRateInternalDto getExchangeRateFromDatabase(LocalDateTime ratioDate, String symbol) {
        LocalDateTime start = ratioDate.minusMinutes(5);
        LocalDateTime end = ratioDate.plusMinutes(5);

        return (currencyRateRepository.findExchangeRateByRatioDateBetween(start, end, symbol))
                .orElseThrow(() -> new NoSuchElementException("Currency rate of " + symbol + " between " + start + " and " + end + " not found"));
    }
}