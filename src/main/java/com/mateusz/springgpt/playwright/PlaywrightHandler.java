package com.mateusz.springgpt.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PlaywrightHandler {

    private final Playwright playwright;
    private static final int[] resolution = {1920, 1080};

    public PlaywrightHandler() {
        log.info("Initializing Playwright...");
        this.playwright = Playwright.create();
    }


    public Browser createBrowser(boolean headless) {
        log.info("Launching new browser instance...");
        return playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(List.of("--disable-blink-features=AutomationControlled",
                        "--disable-features=IsolateOrigins,site-per-process")));
    }

    public Page createPage(Browser browser, boolean mockHuman) {
        if (mockHuman) {
            return browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .setExtraHTTPHeaders((Map.of(
                            "Accept-Language", "en-US,en;q=0.9",
                            "Referer", "https://google.com"
                    )))
                    .setViewportSize(resolution[0], resolution[1])).newPage();
        } else {
            return browser.newContext(new Browser.NewContextOptions().setViewportSize(resolution[0], resolution[1])).newPage();
        }
    }

    public void navigate(Page page, String URL) {
        try {
            log.info("Navigating to: {}", URL);
            page.navigate(URL);
            log.info("Navigation finished.");
        } catch (Exception e) {
            throw new PlaywrightException("Failed to navigate to: " + URL + e);
        }
    }

    public void click(Page page, String cssSelector) {
        Locator locator = page.locator(cssSelector);
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        try {
            log.info("Clicking on element: " + cssSelector);
            locator.click();
        } catch (Exception e) {
            throw new PlaywrightException("Failed to click on the element: " + cssSelector, e);
        }
    }

    public String screenshot(Page page, String namePrefix) {
        String filePath = "target/screenshots/";
        String timestamp = new SimpleDateFormat("_yyyy-MM-dd HH-mm").format(new Date());
        String fileExtension = ".png";

        log.info("Making a screenshot of: {}, on {} to -> {}", page.url(), timestamp, filePath);
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                .setPath(Path.of(filePath + namePrefix + timestamp + fileExtension))
                .setFullPage(true));

        return Base64.getEncoder().encodeToString(screenshot);
    }

    public void closeBrowser(Browser browser) {
        if (browser != null) {
            log.info("Closing browser...");
            browser.close();
        }
    }

    @PreDestroy
    public void closePlaywright() {
        if (playwright != null) {
            log.info("Closing Playwright...");
            playwright.close();
        }
    }
}