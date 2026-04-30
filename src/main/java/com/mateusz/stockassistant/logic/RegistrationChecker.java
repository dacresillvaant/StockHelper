package com.mateusz.stockassistant.logic;

import com.mateusz.stockassistant.service.MailgunEmailService;
import com.mateusz.stockassistant.tools.PlaywrightHandler;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Deprecated
@Component
@Slf4j
public class RegistrationChecker {

    private static final String URL = "https://kwalifikacje.raszeja.poznan.pl";

    @Value("${playwright.headless}")
    private boolean headless;

    private final PlaywrightHandler playwrightHandler;
    private final MailgunEmailService mailgunEmailService;

    @Autowired
    public RegistrationChecker(PlaywrightHandler playwrightHandler, MailgunEmailService mailgunEmailService) {
        this.playwrightHandler = playwrightHandler;
        this.mailgunEmailService = mailgunEmailService;
    }

    public void checkRegistration() {
        Browser browser = playwrightHandler.createBrowser(headless);
        Page page = playwrightHandler.createPage(browser, true);

        playwrightHandler.navigate(page, URL);

        page.selectOption("#visitType", "2");
        page.click("button.submit-button");

        boolean noSlotsAvailable = page.getByText("Niestety, aktualnie nie ma dostępnych terminów").isVisible();

        if (noSlotsAvailable) {
            log.info("No slots available");
        } else {
            log.info("Slots available!");
            String text = "Otwarta rejestracja na oddział";
            mailgunEmailService.sendEmail(mailgunEmailService.getDefaultMailReceiver(), text, text.concat(": ").concat(URL));
        }

        playwrightHandler.closeBrowser(browser);
    }
}