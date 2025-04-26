package com.mateusz.springgpt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class WebClientLoggingUtil {

    private WebClientLoggingUtil() {}

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Request: {} {}", request.method(), request.url());

            return Mono.just(request);
        });
    }

    public static ExchangeFilterFunction logResponse() {
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
}