package com.mateusz.springgpt.service;

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
                .filter(((request, next) -> {
                    log.info("Sending request to Twelve Data: {}", request.url());
                    return next.exchange(request);
                }))
                .build();
    }

    public Mono<ResponseEntity<String>> getUsage() {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("api_usage/")
                        .build())
                .retrieve().toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> getExchangeRate(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                .path("exchange_rate")
                .queryParam("symbol", symbol)
                .build())
                .retrieve().toEntity(String.class);
    }
}