package com.mateusz.springgpt.logic;

import com.mateusz.springgpt.entity.HeatmapEntity;
import com.mateusz.springgpt.repository.HeatmapRepository;
import com.mateusz.springgpt.service.tools.ImageAnalyzer;
import com.mateusz.springgpt.service.tools.PlaywrightHandler;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class HeatMapScrapper {

    private static final String URL = "https://finviz.com/map.ashx?t=sec";

    @Value("${playwright.headless}")
    private boolean headless;

    private final PlaywrightHandler playwrightHandler;
    private final HeatmapRepository heatmapRepository;

    @Autowired
    public HeatMapScrapper(PlaywrightHandler playwrightHandler, HeatmapRepository heatmapRepository) {
        this.playwrightHandler = playwrightHandler;
        this.heatmapRepository = heatmapRepository;
    }

    @Scheduled(cron = "${scheduler.heatmap}")
    public void scrapHeatMap() {
        Browser browser = playwrightHandler.createBrowser(headless);
        Page page = playwrightHandler.createPage(browser, true);

        playwrightHandler.navigate(page, URL);
        playwrightHandler.click(page, "button:has-text('DISAGREE')");
        playwrightHandler.click(page, "button:has(div:has-text('Fullscreen'))");

        byte[] screenshot = playwrightHandler.screenshotSelectedPart(page, "heatMap", "canvas.chart.initialized");
        String base64screenshot = ImageAnalyzer.byteToBase64(screenshot);
        saveHeatmapToDatabase(page, base64screenshot);

        playwrightHandler.closeBrowser(browser);
    }

    private void saveHeatmapToDatabase(Page page, String screenshotInBase64) {
        HeatmapEntity heatmapEntity = HeatmapEntity.builder()
                .createdDate(LocalDateTime.now())
                .source(page.url())
                .base64(screenshotInBase64)
                .build();

        heatmapRepository.save(heatmapEntity);
        log.info("Screenshot was successfully saved in the database.");
    }
}