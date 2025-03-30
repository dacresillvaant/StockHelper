package com.mateusz.springgpt.logic;

import com.mateusz.springgpt.entity.HeatmapEntity;
import com.mateusz.springgpt.repository.HeatmapRepository;
import com.mateusz.springgpt.service.tools.ImageAnalyzer;
import com.mateusz.springgpt.service.tools.PlaywrightHandler;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.mateusz.springgpt.service.tools.ImageAnalyzer.IMAGE_ANALYZER_VERSION;

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
        double heatmapRatio = calculateHeatmapRatio(screenshot).doubleValue();

        saveHeatmapToDatabase(page, base64screenshot, heatmapRatio);

        playwrightHandler.closeBrowser(browser);
    }

    private void saveHeatmapToDatabase(Page page, String base64screenshot, double heatmapRatio) {
        HeatmapEntity heatmapEntity = HeatmapEntity.builder()
                .createdDate(LocalDateTime.now())
                .source(page.url())
                .base64(base64screenshot)
                .ratio(heatmapRatio)
                .modelVersion(IMAGE_ANALYZER_VERSION)
                .build();

        heatmapRepository.save(heatmapEntity);
        log.info("Screenshot was successfully saved in the database.");
    }

    private BigDecimal calculateHeatmapRatio(byte[] screenshot) {
        Mat heatmap = ImageAnalyzer.byteToMat(screenshot);
        return ImageAnalyzer.greenRedRatio(heatmap);
    }
}