package com.mateusz.springgpt.service;

import com.mateusz.springgpt.config.WebClientLoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AlphaVantageService {

    private final WebClient webClient;

    @Value("${alphavantage.api-key}")
    private String apiKey;

    @Autowired
    public AlphaVantageService(@Value("${alphavantage.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logRequest());
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logResponse());
                })
                .build();
    }

    public Mono<ResponseEntity<String>> getNews(String topics, String limit) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                .queryParam("function", "NEWS_SENTIMENT")
                .queryParam("topics", topics)
                .queryParam("limit", limit)
                .queryParam("apikey", apiKey)
                .build())
                .retrieve().toEntity(String.class);
    }
}
