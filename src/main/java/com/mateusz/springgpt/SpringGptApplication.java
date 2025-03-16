package com.mateusz.springgpt;

import com.mateusz.springgpt.playwright.HeatMapScrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringGptApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringGptApplication.class, args);

        HeatMapScrapper heatMapScrapper = new HeatMapScrapper();
        heatMapScrapper.scrapHeatMap();
    }
}