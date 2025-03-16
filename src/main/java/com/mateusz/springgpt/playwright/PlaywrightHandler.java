package com.mateusz.springgpt.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Getter
public class PlaywrightHandler {

    private final Playwright playwright;
    private final Browser browser;
    private static final int[] resolution = {1920, 1080};

    public PlaywrightHandler() {
        log.info("Setting up Playwright...");
        this.playwright = Playwright.create();

        log.info("Setting up browser...");
        this.browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    public Page createPage() {
        return browser.newContext(new Browser.NewContextOptions().setViewportSize(resolution[0], resolution[1])).newPage();
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

    public void screenshot(Page page, String namePrefix) {
        String filePath = "target/screenshots/";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String fileExtension = ".png";

        log.info("Making a screenshot of: {}, on {} to -> {}", page.url(), timestamp, filePath);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Path.of(filePath + namePrefix + timestamp + fileExtension))
                .setFullPage(true));
    }

    public void closeBrowserAndPlaywright() {
        try {
            log.info("Closing browser context...");
            browser.contexts().forEach(BrowserContext::close);

            log.info("Closing browser...");
            browser.close();
        } catch (Exception e) {
            throw new PlaywrightException("Failed to close the browser: " + e);
        }

        try {
            log.info("Closing Playwright...");
            playwright.close();
        } catch (Exception e) {
            throw new PlaywrightException("Failed to close the playwright:" + e);
        }
    }
}