package com.mateusz.stockassistant.logic;

import com.mateusz.stockassistant.entity.HeatmapEntity;
import com.mateusz.stockassistant.repository.HeatmapRepository;
import com.mateusz.stockassistant.tools.ImageAnalyzer;
import com.mateusz.stockassistant.tools.PlaywrightHandler;
import com.mateusz.stockassistant.tools.PlaywrightResourceManager;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.mateusz.stockassistant.tools.ImageAnalyzer.IMAGE_ANALYZER_VERSION;

@Component
@Slf4j
public class HeatMapScrapper {

    private static final String URL = "https://finviz.com/map.ashx?t=sec";

    @Value("${scheduler.heatmap.save-to-target}")
    private boolean saveToTarget;

    private final PlaywrightResourceManager playwrightResourceManager;
    private final PlaywrightHandler playwrightHandler;
    private final HeatmapRepository heatmapRepository;

    @Autowired
    public HeatMapScrapper(PlaywrightResourceManager playwrightResourceManager, PlaywrightHandler playwrightHandler, HeatmapRepository heatmapRepository) {
        this.playwrightResourceManager = playwrightResourceManager;
        this.playwrightHandler = playwrightHandler;
        this.heatmapRepository = heatmapRepository;
    }

    public void scrapHeatMap() {
        playwrightResourceManager.executeInBrowser(page -> {
            try {
                playwrightHandler.navigate(page, URL);
                playwrightHandler.click(page, "button:has-text('Reject all')");
                playwrightHandler.click(page, "button:has(span:has-text('Fullscreen'))");

                byte[] screenshot = playwrightHandler.screenshotSelectedPart(page, "heatMap", "canvas.chart.initialized", saveToTarget);
                String base64screenshot = ImageAnalyzer.byteToBase64(screenshot);
                double heatmapRatio = calculateHeatmapRatio(screenshot).doubleValue();

                saveHeatmapToDatabase(page, base64screenshot, heatmapRatio);
            } catch (Exception e) {
                throw new RuntimeException("Heat map scrapping failed", e);
            }
        });
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