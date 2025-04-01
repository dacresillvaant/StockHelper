package com.mateusz.springgpt.controller;

import com.mateusz.springgpt.service.AlphaVantageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/alphavantage")
public class AlphaVantageController {

    private final AlphaVantageService alphaVantageService;

    @Autowired
    public AlphaVantageController(AlphaVantageService alphaVantageService) {
        this.alphaVantageService = alphaVantageService;
    }

    @GetMapping("/news/")
    public Mono<ResponseEntity<String>> getNews(@RequestParam String topics, @RequestParam String limit) {
        return alphaVantageService.getNews(topics, limit);
    }
}