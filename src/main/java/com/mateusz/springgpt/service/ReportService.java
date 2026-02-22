package com.mateusz.springgpt.service;

import com.mateusz.springgpt.controller.nbp.dto.NbpCurrencyRateDto;
import com.mateusz.springgpt.entity.OwnedStockEntity;
import com.mateusz.springgpt.repository.OwnedStockRepository;
import com.mateusz.springgpt.tools.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ReportService {

    private final OwnedStockRepository ownedStockRepository;
    private final NbpService nbpService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public ReportService(OwnedStockRepository ownedStockRepository, NbpService nbpService) {
        this.ownedStockRepository = ownedStockRepository;
        this.nbpService = nbpService;
    }

    private BigDecimal calculatePrice(BigDecimal symbolPrice, BigDecimal currencyRate) {
        return symbolPrice.multiply(currencyRate).setScale(2, RoundingMode.HALF_UP);
    }

    private NbpCurrencyRateDto getFirstAvailableNbpData(String symbolCurrency, String date) {
        LocalDate dateOf = LocalDate.parse(date, DATE_FORMATTER);
        int retryNumber = 0;

        while (retryNumber < 10) {
            try {
                return nbpService.getPlnCurrencyRateForDate(symbolCurrency, dateOf.format(DATE_FORMATTER)).getBody();
            } catch (Exception e) {
                log.warn("Could not retrieve NBP data for currency rate {} for date {}. Retrying with date +1 day", symbolCurrency, dateOf);
                dateOf = dateOf.plusDays(1);
                retryNumber++;
            }
        }

        throw new IllegalStateException("Could not retrieve NBP currency rate data for " + symbolCurrency + " for date: " + date);
    }

    public void prepareOwnedStockProfitReport() {
        BigDecimal todayUsdPlnCurrencyRate = nbpService.getPlnLastKnownCurrencyRate("USD").getBody().getRates().get(0).getMid();
        BigDecimal todayCadPlnCurrencyRate = nbpService.getPlnLastKnownCurrencyRate("CAD").getBody().getRates().get(0).getMid();

        List<OwnedStockEntity> ownedStocks = ownedStockRepository.findAll();

        for (OwnedStockEntity ownedStock : ownedStocks) {
            log.info("Processing {}", ownedStock.getName());
            String symbolCurrency = ownedStock.getCurrency();
            String purchaseDate = ownedStock.getBoughtDate().format(DATE_FORMATTER);

            BigDecimal purchaseDayXPlnCurrencyRate = getFirstAvailableNbpData(symbolCurrency, purchaseDate).getRates().get(0).getMid();

            BigDecimal todayXPlnCurrencyRate;
            switch (symbolCurrency) {
                case "USD" -> todayXPlnCurrencyRate = todayUsdPlnCurrencyRate;
                case "CAD" -> todayXPlnCurrencyRate =  todayCadPlnCurrencyRate;
                default -> throw new IllegalArgumentException("Unmapped currency: " + symbolCurrency);
            }

            BigDecimal purchaseDayValue = calculatePrice(ownedStock.getPurchasePrice(), purchaseDayXPlnCurrencyRate).multiply(BigDecimal.valueOf(ownedStock.getPosition()));
            //dodac pobieranie dzisiejszej ceny waloru
            BigDecimal todayValue = calculatePrice(ownedStock.getPurchasePrice(), todayXPlnCurrencyRate).multiply(BigDecimal.valueOf(ownedStock.getPosition()));

            log.info("{} purchase price: {}, today price: {}", ownedStock.getName(), purchaseDayValue, todayValue);

            Utils.sleep(100); //to avoid spamming NBP API too much
        }
    }
}