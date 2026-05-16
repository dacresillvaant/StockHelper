package com.mateusz.stockassistant.logic;

import com.mateusz.stockassistant.controller.trend.GeoScope;
import com.mateusz.stockassistant.service.MailgunEmailService;
import com.mateusz.stockassistant.tools.PlaywrightHandler;
import com.mateusz.stockassistant.tools.mail.MailTemplate;
import com.mateusz.stockassistant.tools.mail.MailTemplateFactory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TrendNotifier {

    private static final String BASE_URL = "https://trends.google.com/trending?";
    private static final String GEO_SCOPE = "geo=$";
    private static final String PERIOD = "&hours=168";
    private static final String SORT = "&sort=search-volume";

    @Value("${playwright.headless}")
    private boolean headless;

    private final PlaywrightHandler playwrightHandler;
    private final MailgunEmailService mailgunEmailService;

    public TrendNotifier(PlaywrightHandler playwrightHandler, MailgunEmailService mailgunEmailService) {
        this.playwrightHandler = playwrightHandler;
        this.mailgunEmailService = mailgunEmailService;
    }

    private String cleanData(String data) {
        return data.replace("trending_upActive", "")
                .replace("arrow_upward", "")
                .replace("agotimelapseLasted", "")
                .replace("trending_up", "")
                .replace("timelapse", "")
                .replace("Active", "")
                .replaceAll("Lasted.*", "")
                .replaceAll("\\s*[0-9,]+%", "")
                .trim().strip();
    }

    public record TrendTableRow(String column1, String column2, String column3) {}

    public void checkTrends() {
        Browser browser = playwrightHandler.createBrowser(false);
        Page page = browser.newPage();

        page.navigate(BASE_URL.concat(GEO_SCOPE.replace("$", GeoScope.US.name())).concat(PERIOD).concat(SORT));
        page.waitForSelector("xpath=//*[@id='trend-table']/div[1]/table/tbody[2]/tr", new Page.WaitForSelectorOptions().setTimeout(10000));

        Locator trendsTableRows = page.locator("xpath=//*[@id='trend-table']/div[1]/table/tbody[2]/tr");
        int rowCount = trendsTableRows.count();

        System.out.println("Total rows found: " + rowCount);
        List<TrendNotifier.TrendTableRow> trendsTableData = new java.util.ArrayList<>();;

        // Loop through each row and extract data
        for (int i = 0; i < rowCount; i++) {
            Locator row = trendsTableRows.nth(i);

            String col1 = cleanData(row.locator("td").nth(1).innerText());
            String col2 = cleanData(row.locator("td").nth(2).innerText());
            String col3 = cleanData(row.locator("td").nth(3).innerText());

            trendsTableData.add(new TrendTableRow(col1, col2, col3));

            System.out.printf("""
                    ##### START OF ROW #####
                    Row: %d
                    Trend: %s
                    Search volume: %s
                    Started: %s
                    ##### END OF ROW #####
                    %n
                    """, i, col1, col2, col3);
        }

        MailTemplate mailTemplate = MailTemplateFactory.trendTemplate(GeoScope.US, trendsTableData);
        mailgunEmailService.sendEmail(mailgunEmailService.getDefaultMailReceiver(), mailTemplate);

        page.close();
        browser.close();
    }
}
