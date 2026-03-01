package com.mateusz.stockassistant.service;

import com.mateusz.stockassistant.config.WebClientLoggingUtil;
import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooDetailedChartResponseDto;
import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooTruncatedChartResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    public ResponseEntity<String> getData(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/")
                        .path(symbol)
                        .build())
                .retrieve().toEntity(String.class).block();
    }

    /**
     * @param symbol ticker
     * @param range "1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y", "10y", "ytd", "max"
     * @param interval "1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y", "10y", "ytd", "max"
     * @return
     */
    public ResponseEntity<String> getData(String symbol, String range, String interval) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/".concat(symbol))
                        .queryParam("range", range)
                        .queryParam("interval", interval)
                        .build())
                .retrieve().toEntity(String.class).block();
    }

    public ResponseEntity<YahooTruncatedChartResponseDto> getSimplifiedData(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/" + symbol)
                        .queryParam("range", "1y")
                        .queryParam("interval", "1mo")
                        .build())
                .retrieve().toEntity(YahooTruncatedChartResponseDto.class).block();
    }

    /**
     * @param symbol ticker
     * @param range "1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y", "10y", "ytd", "max"
     * @param interval "1d", "5d", "1mo", "3mo", "6mo", "1y", "2y", "5y", "10y", "ytd", "max"
     * @return {@link YahooDetailedChartResponseDto}
     */
    public YahooDetailedChartResponseDto getDetailedData(String symbol, String range, String interval) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/".concat(symbol))
                        .queryParam("range", range)
                        .queryParam("interval", interval)
                        .build())
                .retrieve().bodyToMono(YahooDetailedChartResponseDto.class).block();
    }
}