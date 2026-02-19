package com.mateusz.springgpt.service;

import com.mateusz.springgpt.config.WebClientLoggingUtil;
import com.mateusz.springgpt.controller.yahoofinance.dto.YahooTruncatedChartResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class YahooFinanceService {

    private final WebClient webClient;

    @Autowired
    public YahooFinanceService(@Value("${yahoofinance.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logRequest());
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logResponse());
                })
                .defaultHeader(HttpHeaders.USER_AGENT,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/120.0.0.0 Safari/537.36")
                .build();
    }

    public Mono<ResponseEntity<String>> getData(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/")
                        .path(symbol)
                        .build())
                .retrieve().toEntity(String.class);
    }

    public Mono<ResponseEntity<String>> getData(String symbol, String range, String interval) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/".concat(symbol))
                        .queryParam("range", range)
                        .queryParam("interval", interval)
                        .build())
                .retrieve().toEntity(String.class);
    }

    public Mono<ResponseEntity<YahooTruncatedChartResponseDto>> getSimplifiedData(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/" + symbol)
                        .queryParam("range", "1y")
                        .queryParam("interval", "1mo")
                        .build())
                .exchangeToMono(response -> {
                    HttpStatusCode status = response.statusCode();

                    return response.bodyToMono(YahooTruncatedChartResponseDto.class)
                            .map(body -> ResponseEntity.status(status).body(body))
                            .defaultIfEmpty(ResponseEntity.status(status).build());
                });
    }
}