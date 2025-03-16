package com.mateusz.springgpt.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HeatMapScrapper {

    private static final String URL = "https://finviz.com/map.ashx?t=sec";

    @Value("${playwright.headless}")
    private boolean headless;
    private final PlaywrightHandler playwrightHandler;

    @Autowired
    public HeatMapScrapper(PlaywrightHandler playwrightHandler) {
        this.playwrightHandler = playwrightHandler;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void scrapHeatMap() {
        Browser browser = playwrightHandler.createBrowser(headless);
        Page page = playwrightHandler.createPage(browser);

        playwrightHandler.navigate(page, URL);
        playwrightHandler.click(page, "button:has-text('DISAGREE')");
        playwrightHandler.click(page, "button:has(div:has-text('Fullscreen'))");
        playwrightHandler.screenshot(page, "heatMap");

        playwrightHandler.closeBrowser(browser);
    }
}