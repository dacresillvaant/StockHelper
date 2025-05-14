package com.mateusz.springgpt.logic;

import com.mateusz.springgpt.controller.dto.CurrencyRateExternalDto;
import com.mateusz.springgpt.controller.dto.CurrencyRateInternalDto;
import com.mateusz.springgpt.entity.CurrencyRateEntity;
import com.mateusz.springgpt.repository.CurrencyRateRepository;
import com.mateusz.springgpt.service.MailgunEmailService;
import com.mateusz.springgpt.service.TwelveDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@Slf4j
public class CurrencyRateNotifier {

    private final TwelveDataService twelveDataService;
    private final CurrencyRateRepository currencyRateRepository;
    private final MailgunEmailService mailgunEmailService;

    @Value("${mailgun.default-receiver}")
    private String mailReceiver;

    @Autowired
    public CurrencyRateNotifier(TwelveDataService twelveDataService, CurrencyRateRepository currencyRateRepository,
                                MailgunEmailService mailgunEmailService) {
        this.twelveDataService = twelveDataService;
        this.currencyRateRepository = currencyRateRepository;
        this.mailgunEmailService = mailgunEmailService;
    }

    private void saveRateToDatabase(CurrencyRateExternalDto currencyRateResponse) {
        CurrencyRateEntity currencyRateEntity = CurrencyRateEntity.builder()
                .createdDate(LocalDateTime.now())
                .ratioDate(LocalDateTime.now())
                .symbol(currencyRateResponse.getSymbol())
                .rate(currencyRateResponse.getRate())
                .build();

        currencyRateRepository.save(currencyRateEntity);
        log.info("Currency rate of {} has been saved to the database.", currencyRateEntity.getSymbol());
    }

    private Optional<CurrencyRateInternalDto> findPreviousRateData(String symbol, String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "day" -> {
                return Optional.ofNullable(twelveDataService.getExchangeRateFromDatabase(now.minusHours(24), symbol));
            }
            case "week" -> {
                return Optional.ofNullable(twelveDataService.getExchangeRateFromDatabase(now.minusHours(168), symbol));
            }
            default -> throw new IllegalStateException("Unexpected value: " + period);
        }
    }

    private String calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return "NaN";
        } else {
            BigDecimal diff = newValue.subtract(oldValue);
            BigDecimal percentageDiff = diff.divide(oldValue, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);

            return percentageDiff.toString();
        }
    }

    private void sendRateEmail(CurrencyRateExternalDto currencyRateResponse, String symbol) {
        Optional<CurrencyRateInternalDto> dayBeforeData = findPreviousRateData(symbol, "day");
        Optional<CurrencyRateInternalDto> weekBeforeData = findPreviousRateData(symbol, "week");

        BigDecimal dayBeforeRate = weekBeforeData.map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);
        BigDecimal weekBeforeRate = dayBeforeData.map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);

        BigDecimal currentRate = currencyRateResponse.getRate();
        String rateChangeDayBefore = calculatePercentageChange(dayBeforeRate, currentRate);
        String rateChangeWeekBefore = calculatePercentageChange(weekBeforeRate, currentRate);

        long timestamp = currencyRateResponse.getTimestamp();
        String formattedTimestamp = Instant.ofEpochSecond(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        String to = mailReceiver;
        String subject = String.format("Currency rate report for: %s %s", currencyRateResponse.getSymbol(), formattedTimestamp);
        String mailBody = String.format("""
                Currency rate of %s is %s - %s
                Change 1D is - %s%%
                Change 7D is - %s%%
                """, currencyRateResponse.getSymbol(), currencyRateResponse.getRate(), formattedTimestamp, rateChangeDayBefore, rateChangeWeekBefore);

        mailgunEmailService.sendEmail(to, subject, mailBody);
    }

    private void processCurrencyRate(String symbol) {
        CurrencyRateExternalDto currencyRateResponse = twelveDataService
                .getExchangeRate(symbol)
                .map(ResponseEntity::getBody)
                .block();

        saveRateToDatabase(currencyRateResponse);
        sendRateEmail(currencyRateResponse, symbol);
    }

    @Scheduled(cron = "${scheduler.usdpln}")
    public void processUsdPln() {
        processCurrencyRate("USD/PLN");
    }
}