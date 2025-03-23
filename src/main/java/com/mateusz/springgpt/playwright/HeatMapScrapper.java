package com.mateusz.springgpt.playwright;

import com.mateusz.springgpt.entity.Screenshot;
import com.mateusz.springgpt.repository.ScreenshotRepository;
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
    private final ScreenshotRepository screenshotRepository;

    @Autowired
    public HeatMapScrapper(PlaywrightHandler playwrightHandler, ScreenshotRepository screenshotRepository) {
        this.playwrightHandler = playwrightHandler;
        this.screenshotRepository = screenshotRepository;
    }

    @Scheduled(cron = "${scheduler.heatmap}")
    public void scrapHeatMap() {
        Browser browser = playwrightHandler.createBrowser(headless);
        Page page = playwrightHandler.createPage(browser, true);

        playwrightHandler.navigate(page, URL);
        playwrightHandler.click(page, "button:has-text('DISAGREE')");
        playwrightHandler.click(page, "button:has(div:has-text('Fullscreen'))");
        String base64screenshot = playwrightHandler.screenshot(page, "heatMap");
        saveScreenshotToDatabase(page, base64screenshot);

        playwrightHandler.closeBrowser(browser);
    }

    private void saveScreenshotToDatabase(Page page, String screenshotInBase64) {
        Screenshot screenshot = Screenshot.builder()
                .createdDate(LocalDateTime.now())
                .source(page.url())
                .base64(screenshotInBase64)
                .build();

        screenshotRepository.save(screenshot);
        log.info("Screenshot was successfully saved in the database.");
    }
}