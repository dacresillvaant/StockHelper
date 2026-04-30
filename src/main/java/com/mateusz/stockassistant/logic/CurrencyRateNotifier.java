package com.mateusz.stockassistant.logic;

import com.mateusz.stockassistant.controller.twelvedata.dto.CurrencyRateInternalDto;
import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooTruncatedChartResponseDto;
import com.mateusz.stockassistant.entity.CurrencyRateEntity;
import com.mateusz.stockassistant.repository.CurrencyRateRepository;
import com.mateusz.stockassistant.service.CurrencyRateService;
import com.mateusz.stockassistant.service.MailgunEmailService;
import com.mateusz.stockassistant.service.YahooFinanceService;
import com.mateusz.stockassistant.tools.mail.MailTemplate;
import com.mateusz.stockassistant.tools.mail.MailTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@Slf4j
public class CurrencyRateNotifier {

    private final CurrencyRateService twelveDataService;
    private final YahooFinanceService yahooFinanceService;
    private final CurrencyRateRepository currencyRateRepository;
    private final MailgunEmailService mailgunEmailService;

    @Autowired
    public CurrencyRateNotifier(CurrencyRateService twelveDataService, com.mateusz.stockassistant.repository.CurrencyRateRepository currencyRateRepository,
                                MailgunEmailService mailgunEmailService, YahooFinanceService yahooFinanceService) {
        this.twelveDataService = twelveDataService;
        this.currencyRateRepository = currencyRateRepository;
        this.mailgunEmailService = mailgunEmailService;
        this.yahooFinanceService = yahooFinanceService;
    }

    private void saveRateToDatabase(YahooTruncatedChartResponseDto currencyRateResponse) {
        CurrencyRateEntity currencyRateEntity = CurrencyRateEntity.builder()
                .createdDate(LocalDateTime.now())
                .ratioDate(LocalDateTime.now())
                .symbol(currencyRateResponse.getMeta().getSymbol())
                .rate(currencyRateResponse.getMeta().getLastPrice())
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

    private void sendRateEmail(YahooTruncatedChartResponseDto currencyRateResponse, String symbol) {
        BigDecimal currentRate = currencyRateResponse.getMeta().getLastPrice();

        BigDecimal dayBeforeRate = findPreviousRateData(symbol, "day").map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);
        BigDecimal weekBeforeRate = findPreviousRateData(symbol, "week").map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);
        BigDecimal monthBeforeRate = findPreviousRateData(symbol, "month").map(CurrencyRateInternalDto::getRate).orElse(BigDecimal.ZERO);

        String rateChangeDayBefore = calculatePercentageChange(dayBeforeRate, currentRate);
        String rateChangeWeekBefore = calculatePercentageChange(weekBeforeRate, currentRate);
        String rateChangeMonthBefore = calculatePercentageChange(monthBeforeRate, currentRate);

        MailTemplate mailTemplate = MailTemplateFactory.currencyRateTemplate(currencyRateResponse, rateChangeDayBefore, rateChangeWeekBefore, rateChangeMonthBefore);
        mailgunEmailService.sendEmail(mailgunEmailService.getDefaultMailReceiver(), mailTemplate);
    }

    public void processCurrencyRate(String symbol) {
        YahooTruncatedChartResponseDto currencyRateResponse = yahooFinanceService.getSimplifiedData(symbol);

        saveRateToDatabase(currencyRateResponse);
        sendRateEmail(currencyRateResponse, symbol);
    }
}