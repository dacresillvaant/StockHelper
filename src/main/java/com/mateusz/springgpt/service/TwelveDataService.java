package com.mateusz.springgpt.service;

import com.mateusz.springgpt.config.WebClientLoggingUtil;
import com.mateusz.springgpt.controller.dto.CurrencyRateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Free API key is limited to 8 credits/minute & 800/day
 */
@Slf4j
@Service
public class TwelveDataService {

    private final WebClient webClient;

    @Autowired
    public TwelveDataService(@Value("${twelvedata.url}") String baseUrl,
                             @Value("${twelvedata.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "apikey " + apiKey)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logRequest());
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logResponse());
                })
                .build();
    }

    public Mono<ResponseEntity<String>> getUsage() {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("api_usage")
                        .build())
                .retrieve().toEntity(String.class);
    }

    public Mono<ResponseEntity<CurrencyRateDto>> getExchangeRate(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("exchange_rate")
                        .queryParam("symbol", symbol)
                        .build())
                .retrieve().toEntity(CurrencyRateDto.class);
    }
}