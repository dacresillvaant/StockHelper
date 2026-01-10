package com.mateusz.springgpt.logic;

import com.mateusz.springgpt.controller.twelvedata.dto.CurrencyRateExternalDto;
import com.mateusz.springgpt.controller.twelvedata.dto.CurrencyRateInternalDto;
import com.mateusz.springgpt.entity.CurrencyRateEntity;
import com.mateusz.springgpt.repository.CurrencyRateRepository;
import com.mateusz.springgpt.service.MailgunEmailService;
import com.mateusz.springgpt.service.TwelveDataService;
import com.mateusz.springgpt.tools.mail.MailTemplate;
import com.mateusz.springgpt.tools.mail.MailTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Slf4j
public class CurrencyRateNotifier {

    private final TwelveDataService twelveDataService;
    private final CurrencyRateRepository currencyRateRepository;
    private final MailgunEmailService mailgunEmailService;

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
        try {
            return switch (period) {
                case "day" -> Optional.ofNullable(twelveDataService.getExchangeRateFromDatabase(now.minusHours(24), symbol));
                case "week" -> Optional.ofNullable(twelveDataService.getExchangeRateFromDatabase(now.minusHours(168), symbol));
                case "month" -> Optional.ofNullable(twelveDataService.getExchangeRateFromDatabase(now.minusHours(720), symbol));

                default -> throw new IllegalStateException("Unexpected value: " + period);
            };
        } catch (NoSuchElementException e) {
            log.warn(e.getMessage());
            return Optional.empty();
        }
    }

    private String calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return "NaN";
        } else {
            BigDecimal diff = newValue.subtract(oldValue);
            BigDecimal percentageDiff = diff.divide(oldValue, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(3, RoundingMode.HALF_UP);

            return percentageDiff.toString();
        }
    }

    private void sendRateEmail(CurrencyRateExternalDto currencyRateResponse, String symbol) {
        BigDecimal currentRate = currencyRateResponse.getRate();

        BigDecimal dayBeforeRate = findPreviousRateData(symbol, "day").map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);
        BigDecimal weekBeforeRate = findPreviousRateData(symbol, "week").map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);
        BigDecimal monthBeforeRate = findPreviousRateData(symbol, "month").map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);

        String rateChangeDayBefore = calculatePercentageChange(dayBeforeRate, currentRate);
        String rateChangeWeekBefore = calculatePercentageChange(weekBeforeRate, currentRate);
        String rateChangeMonthBefore = calculatePercentageChange(monthBeforeRate, currentRate);

        MailTemplate mailTemplate = MailTemplateFactory.currencyRateTemplate(currencyRateResponse,  rateChangeDayBefore, rateChangeWeekBefore, rateChangeMonthBefore);
        mailgunEmailService.sendEmail(mailgunEmailService.getDefaultMailReceiver(), mailTemplate);
    }

    public void processCurrencyRate(String symbol) {
        CurrencyRateExternalDto currencyRateResponse = twelveDataService
                .getExchangeRate(symbol)
                .map(ResponseEntity::getBody)
                .block();

        saveRateToDatabase(currencyRateResponse);
        sendRateEmail(currencyRateResponse, symbol);
    }
}