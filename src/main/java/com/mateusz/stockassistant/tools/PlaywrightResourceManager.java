package com.mateusz.stockassistant.tools;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class PlaywrightResourceManager {

    @Value("${playwright.headless}")
    private boolean headless;

    private final PlaywrightHandler playwrightHandler;

    public PlaywrightResourceManager(PlaywrightHandler playwrightHandler) {
        this.playwrightHandler = playwrightHandler;
    }

    public synchronized <T> T executeInBrowser(Function<Page, T> action) {
        Browser browser = playwrightHandler.createBrowser(headless);

        try {
            try (Page page = playwrightHandler.createPage(browser, true)) {
                return action.apply(page);
            }
        } finally {
            playwrightHandler.closeBrowser(browser);
        }
    }

    public synchronized void executeInBrowser(Consumer<Page> action) {
        Browser browser = playwrightHandler.createBrowser(headless);

        try {
            try (Page page = playwrightHandler.createPage(browser, true)) {
                action.accept(page);
            }
        } finally {
            playwrightHandler.closeBrowser(browser);
        }
    }
}