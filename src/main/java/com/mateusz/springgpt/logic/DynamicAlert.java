package com.mateusz.springgpt.logic;

import com.mateusz.springgpt.controller.dto.QuoteExternalDto;
import com.mateusz.springgpt.service.MailgunEmailService;
import com.mateusz.springgpt.service.TwelveDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@Slf4j
public class DynamicAlert {

    private final TwelveDataService twelveDataService;
    private final MailgunEmailService mailgunEmailService;
    private record AlertConfig(String symbol, int percent) {}

    @Value("${mailgun.default-receiver}")
    private String mailReceiver;

    @Autowired
    public DynamicAlert(TwelveDataService twelveDataService, MailgunEmailService mailgunEmailService) {
        this.twelveDataService = twelveDataService;
        this.mailgunEmailService = mailgunEmailService;
    }

    public void lowPriceAlert(String symbol, int percentChange) {
        QuoteExternalDto quote = twelveDataService.getQuote(symbol).blockOptional().orElseThrow().getBody();
        BigDecimal lastClose = new BigDecimal(quote.getClose());
        BigDecimal yearHigh = new BigDecimal(quote.getFiftyTwoWeek().getHigh());

        BigDecimal percentThreshold = new BigDecimal(100 - percentChange).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        BigDecimal alertThreshold = yearHigh.multiply(percentThreshold);

        log.info("Symbol: \"{}\" - last close price: {}, year high: {}, threshold for alert: {}", symbol, lastClose, yearHigh, alertThreshold);

        String symbolFullName = quote.getName();
        String mailSubject = "ALERT - ".concat(symbolFullName).concat(" fallen below threshold price");
        String mailBody = String.format("""
                Latest price of %s %s is: %s
                Year high is: %s
                Threshold was set to: %s%% -> %s
                """, symbol, symbolFullName, lastClose, yearHigh, percentChange, alertThreshold);

        if (lastClose.compareTo(alertThreshold) < 0) {
            mailgunEmailService.sendEmail(mailReceiver, mailSubject, mailBody);
        }
    }

    @Scheduled(cron = "${scheduler.low-price-alert}")
    public void scheduledLowPriceAlert() {
        List<AlertConfig> alertConfigurations = List.of(
                new AlertConfig("V", 15),
                new AlertConfig("MA", 15)
        );
        alertConfigurations.forEach(alert -> lowPriceAlert(alert.symbol(), alert.percent()));
    }
}