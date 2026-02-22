package com.mateusz.springgpt.service;

import com.mateusz.springgpt.config.WebClientLoggingUtil;
import com.mateusz.springgpt.controller.nbp.dto.NbpCurrencyRateDto;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Slf4j
@Service
public class NbpService {

    private final WebClient webClient;

    @Autowired
    public NbpService(@Value("${nbp.url}") String baseUrl) throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext));

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logRequest());
                    exchangeFilterFunctions.add(WebClientLoggingUtil.logResponse());
                })
                .build();
    }

    public ResponseEntity<NbpCurrencyRateDto> getPlnLastKnownCurrencyRate(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/exchangerates/rates/a/" + symbol + "/").build())
                .retrieve().toEntity(NbpCurrencyRateDto.class).block();
    }

    /**
     * @param symbol USD, EUR etc.
     * @param date YYYY-MM-DD format
     * @return
     */
    public ResponseEntity<NbpCurrencyRateDto> getPlnCurrencyRateForDate(String symbol, String date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/exchangerates/rates/a/" + symbol + "/" + date).build())
                .retrieve().toEntity(NbpCurrencyRateDto.class).block();
    }
}