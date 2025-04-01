package com.mateusz.springgpt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AlphaVantageService {

    private final WebClient webClient;

    @Value("${alphavantage.api-key}")
    private String apiKey;

    @Autowired
    public AlphaVantageService(@Value("${alphavantage.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<String> getNews() {
        return webClient.get().uri(uriBuilder -> uriBuilder
                .queryParam("function", "NEWS_SENTIMENT")
                .queryParam("topics", "economy_macro")
                .queryParam("limit", "100")
                .queryParam("apikey", apiKey)
                .build())
                .retrieve().bodyToMono(String.class);
    }
}
