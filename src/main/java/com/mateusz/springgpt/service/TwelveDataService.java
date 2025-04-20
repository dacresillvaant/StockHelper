package com.mateusz.springgpt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
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
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                })
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Request: {} {}", request.method(), request.url());

            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response ->
                response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .flatMap(body -> {
                            log.info("""
                                    Response from: {} {}
                                    Status: {}
                                    Response body: {}
                                    """, response.request().getMethod(), response.request().getURI(), response.statusCode(), body);

                            // Rebuild the client response so it can be consumed again downstream
                            ClientResponse newResponse = ClientResponse.create(response.statusCode())
                                    .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                                    .body(body)
                                    .build();

                            return Mono.just(newResponse);
                        })
        );
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