package com.mateusz.stockassistant.service.yahoofinance.frontend;

import com.mateusz.stockassistant.service.mailgunemail.MailgunEmailService;
import com.mateusz.stockassistant.tools.PlaywrightHandler;
import com.mateusz.stockassistant.tools.PlaywrightResourceManager;
import com.mateusz.stockassistant.tools.Utils;
import com.mateusz.stockassistant.tools.mail.MailTemplate;
import com.mateusz.stockassistant.tools.mail.MailTemplateFactory;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class YahooFinanceAnalystInsightsService {

    private static final String BASE_TICKER_URL = "https://finance.yahoo.com/quote/$ticker/analyst-insights/";
    private static final String BASE_TICKERS_SOURCE_URL = "https://finance.yahoo.com/markets/stocks/$type/";

    private static final String REJECT_COOKIES = "button[type='submit'][name='reject']";

    private static final String CURRENT_PRICE = "div:has(> span:text('Current')) > span.price";
    private static final String AVERAGE_PRICE = "div[class*='label'][class*='average'] > span";
    private static final String HIGH_PRICE = "div[class*='priceContainer'][class*='high'] span";
    private static final String LOW_PRICE = "div[class*='priceContainer'][class*='low'] span";

    private static final String TABLE_ROW = "div[data-testid='data-table-v2'] tr";
    private static final String TICKER_CELL = "td[data-testid-cell='ticker'] span.symbol";

    private final PlaywrightResourceManager playwrightResourceManager;
    private final PlaywrightHandler playwrightHandler;
    private final MailgunEmailService mailgunEmailService;

    @Autowired
    public YahooFinanceAnalystInsightsService(PlaywrightResourceManager playwrightResourceManager,
                                              PlaywrightHandler playwrightHandler, MailgunEmailService mailgunEmailService) {
        this.playwrightResourceManager = playwrightResourceManager;
        this.playwrightHandler = playwrightHandler;
        this.mailgunEmailService = mailgunEmailService;
    }

    private void rejectCookies(Page page) {
        try {
            page.locator(REJECT_COOKIES).waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(2000));

            log.info("Rejecting cookies");
            page.locator(REJECT_COOKIES).click();
        } catch (TimeoutError e) {
            log.debug("Cookie dialog not present, skipping");
        }
    }

    private List<String> getTickersFromTable(Page page, String tableSelector, String tickerSelector) {
        List<String> tickers = new ArrayList<>();

        Locator tableRows = page.locator(tableSelector);
        tableRows.first().waitFor();
        int rowCount = tableRows.count();

        for (int i = 0; i < rowCount; i++) {
            Locator row = tableRows.nth(i);
            row.scrollIntoViewIfNeeded();

            Locator tickerLocator = row.locator(tickerSelector);

            if (tickerLocator.count() == 0) {
                log.warn("Row {} has no ticker cell, skipping. HTML: {}", i, row.innerHTML());
            } else {
                String ticker = tickerLocator.first().innerText().trim();
                tickers.add(ticker);
            }
        }

        log.info("{} Tickers found: {}", tickers.size(), tickers);
        return tickers;
    }

    private List<String> getStocksOfInterest(StockType stockType) {
        return playwrightResourceManager.executeInBrowser(page -> {
            playwrightHandler.navigate(page, BASE_TICKERS_SOURCE_URL.replace("$type", stockType.getDescription()));
            page.waitForLoadState();
            rejectCookies(page);

            return getTickersFromTable(page, TABLE_ROW, TICKER_CELL);
        });
    }

    private void checkSingleStockAnalystInsights(Page page, String ticker) {
        playwrightHandler.navigate(page, BASE_TICKER_URL.replace("$ticker", ticker));
        page.waitForLoadState();
        rejectCookies(page);

        String current = page.locator(CURRENT_PRICE).first().textContent();
        String average = page.locator(AVERAGE_PRICE).first().textContent();
        String high = page.locator(HIGH_PRICE).first().textContent();
        String low = page.locator(LOW_PRICE).first().textContent();

        log.info("Ticker: {} price -> Low: {} | Current: {} | Average: {} | High: {}", ticker, low, current, average, high);

        BigDecimal currentPrice = new BigDecimal(current);
        BigDecimal lowPrice = new BigDecimal(low);

        if (currentPrice.compareTo(lowPrice) < 0) {
            MailTemplate mailTemplate = MailTemplateFactory.analystInsightsTemplate(ticker, currentPrice, lowPrice);
            mailgunEmailService.sendEmail(mailgunEmailService.getDefaultMailReceiver(), mailTemplate);
        }
    }

    public void checkAnalystInsightsOfStocks(StockType stockType) {
        List<String> tickers = getStocksOfInterest(stockType);

        playwrightResourceManager.executeInBrowser(page -> {
            tickers.forEach(ticker -> {
                try {
                    checkSingleStockAnalystInsights(page, ticker);
                    Utils.sleep(500); //to avoid spamming yahoo finance too much
                } catch (Exception e) {
                    log.error("Error checking stock analysis for ticker: {}", ticker, e);
                }
            });
        });
    }
}