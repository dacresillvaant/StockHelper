package com.mateusz.stockassistant.service;

import com.mateusz.stockassistant.controller.nbp.dto.NbpCurrencyRateDto;
import com.mateusz.stockassistant.controller.report.dto.ProfitReportDto;
import com.mateusz.stockassistant.entity.OwnedStockEntity;
import com.mateusz.stockassistant.repository.OwnedStockRepository;
import com.mateusz.stockassistant.tools.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ReportService {

    private final OwnedStockRepository ownedStockRepository;
    private final NbpService nbpService;
    private final YahooFinanceService yahooFinanceService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public ReportService(OwnedStockRepository ownedStockRepository, NbpService nbpService, YahooFinanceService yahooFinanceService) {
        this.ownedStockRepository = ownedStockRepository;
        this.nbpService = nbpService;
        this.yahooFinanceService = yahooFinanceService;
    }

    private BigDecimal calculateValue(BigDecimal symbolPrice, BigDecimal currencyRate, int position) {
        return symbolPrice.multiply(currencyRate).multiply(BigDecimal.valueOf(position)).setScale(2, RoundingMode.HALF_UP);
    }

    private NbpCurrencyRateDto getFirstAvailableNbpData(String symbolCurrency, String date) {
        LocalDate dateOf = LocalDate.parse(date, DATE_FORMATTER);
        int retryNumber = 0;

        while (retryNumber < 10) {
            try {
                return nbpService.getPlnCurrencyRateForDate(symbolCurrency, dateOf.format(DATE_FORMATTER)).getBody();
            } catch (Exception e) {
                log.warn("Could not retrieve NBP data for currency rate {} for date {}. Retrying with date -1 day", symbolCurrency, dateOf);
                dateOf = dateOf.minusDays(1);
                retryNumber++;
            }
        }

        throw new IllegalStateException("Could not retrieve NBP currency rate data for " + symbolCurrency + " for date: " + date);
    }

    public List<ProfitReportDto> prepareOwnedStockProfitReport() {
        log.info("START: Preparing owned stock profit report");

        BigDecimal todayUsdPlnCurrencyRate = nbpService.getPlnLastKnownCurrencyRate("USD").getBody().getRates().get(0).getMid();
        BigDecimal todayCadPlnCurrencyRate = nbpService.getPlnLastKnownCurrencyRate("CAD").getBody().getRates().get(0).getMid();

        List<OwnedStockEntity> ownedStocks = ownedStockRepository.findAll();
        LinkedList<ProfitReportDto> results = new LinkedList<>();

        for (OwnedStockEntity os : ownedStocks) {
            log.info("Processing {}", os.getName());
            String symbolCurrency = os.getCurrency();
            String purchaseDate = os.getBoughtDate().format(DATE_FORMATTER);

            BigDecimal purchaseDayXPlnCurrencyRate = getFirstAvailableNbpData(symbolCurrency, purchaseDate).getRates().get(0).getMid();

            BigDecimal todayXPlnCurrencyRate;
            switch (symbolCurrency) {
                case "USD" -> todayXPlnCurrencyRate = todayUsdPlnCurrencyRate;
                case "CAD" -> todayXPlnCurrencyRate =  todayCadPlnCurrencyRate;
                default -> throw new IllegalArgumentException("Unmapped currency: " + symbolCurrency);
            }

            BigDecimal lastPrice = yahooFinanceService.getSimplifiedData(os.getTicker()).getBody().getChart().getResult().get(0).getMeta().getLastPrice();

            BigDecimal purchaseDayValue = calculateValue(os.getPurchasePrice(), purchaseDayXPlnCurrencyRate, os.getPosition());
            BigDecimal todayValue = calculateValue(lastPrice, todayXPlnCurrencyRate, os.getPosition());

            BigDecimal priceChange = todayValue.subtract(purchaseDayValue);

            ProfitReportDto reportDataRow = ProfitReportDto.builder()
                    .name(os.getName())
                    .ticker(os.getTicker())
                    .purchaseDate(LocalDate.parse(purchaseDate))
                    .purchaseCurrencyRateToPLN(purchaseDayXPlnCurrencyRate)
                    .todayCurrencyRateToPLN(todayXPlnCurrencyRate)
                    .purchasePrice(new ProfitReportDto.PriceWithCurrency(calculateValue(os.getPurchasePrice(), BigDecimal.ONE, os.getPosition()), os.getCurrency()))
                    .purchasePriceConverted(new ProfitReportDto.PriceWithCurrency(purchaseDayValue, "PLN"))
                    .todayPrice(new ProfitReportDto.PriceWithCurrency(calculateValue(lastPrice, BigDecimal.ONE, os.getPosition()), os.getCurrency()))
                    .todayPriceConverted(new ProfitReportDto.PriceWithCurrency(todayValue, "PLN"))
                    .diff(new ProfitReportDto.PriceWithCurrency(calculateValue(lastPrice, BigDecimal.ONE, os.getPosition()).subtract(calculateValue(os.getPurchasePrice(), BigDecimal.ONE, os.getPosition())), os.getCurrency()))
                    .diffConverted(new ProfitReportDto.PriceWithCurrency(priceChange, "PLN"))
                    .build();

            results.add(reportDataRow);

            Utils.sleep(100); //to avoid spamming NBP & Yahoo Finance APIs too much
        }

        results.forEach(result -> log.info("{} purchase price: {}, today price: {}, profit/loss: {}",
                result.getName(), result.getPurchasePrice(), result.getTodayPrice(), result.getDiff()));

        log.info("FINISH: Preparing owned stock profit report");

        return results;
    }
}