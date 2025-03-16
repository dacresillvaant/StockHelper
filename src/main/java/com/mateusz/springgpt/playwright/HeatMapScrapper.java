package com.mateusz.springgpt.playwright;

import com.microsoft.playwright.Page;

public class HeatMapScrapper {

    private static final String URL = "https://finviz.com/map.ashx?t=sec";

    public void scrapHeatMap() {
        PlaywrightHandler playwrightHandler = new PlaywrightHandler();
        Page page = playwrightHandler.createPage();

        page.navigate(URL);
        playwrightHandler.click(page, "button:has-text('DISAGREE')");
        playwrightHandler.click(page, "button:has(div:has-text('Fullscreen'))");
        playwrightHandler.screenshot(page, "heatMap");

        playwrightHandler.closeBrowserAndPlaywright();
    }
}