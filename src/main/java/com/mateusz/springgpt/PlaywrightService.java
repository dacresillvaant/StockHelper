package com.mateusz.springgpt;

import com.microsoft.playwright.*;
import lombok.Getter;

@Getter
public class PlaywrightService {

    private final Playwright playwright;
    private final Browser browser;

    public PlaywrightService() {
        this.playwright = Playwright.create();
        this.browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    public Page createPage() {
        return browser.newContext().newPage();
    }

    public void closeBrowserAndPlaywright() {
        try {
            browser.contexts().forEach(BrowserContext::close);
            browser.close();
        } catch (Exception e) {
            throw new PlaywrightException("Failed to close the browser: " + e);
        }

        try {
            playwright.close();
        } catch (Exception e) {
            throw new PlaywrightException("Failed to close the playwright:" + e);
        }
    }
}