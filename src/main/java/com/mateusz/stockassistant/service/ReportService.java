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
                return nbpService.getPlnCurrencyRateForDate(symbolCurrency, dateOf.format(DATE_FORMATTER));
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

        BigDecimal todayUsdPlnCurrencyRate = nbpService.getPlnLastKnownCurrencyRate("USD").getRates().get(0).getMid();
        BigDecimal todayCadPlnCurrencyRate = nbpService.getPlnLastKnownCurrencyRate("CAD").getRates().get(0).getMid();
        BigDecimal todayGbpPlnCurrencyRate = nbpService.getPlnLastKnownCurrencyRate("GBP").getRates().get(0).getMid();

        List<OwnedStockEntity> ownedStocks = ownedStockRepository.findAll();
        LinkedList<ProfitReportDto> results = new LinkedList<>();

        for (OwnedStockEntity os : ownedStocks) {
            log.info("Processing {}", os.getName());
            String ticker = os.getTicker();
            String symbolCurrency = os.getCurrency();
            String purchaseDate = os.getBoughtDate().format(DATE_FORMATTER);

            BigDecimal purchaseDayXPlnCurrencyRate;
            if (symbolCurrency.equalsIgnoreCase("PLN")) {
                purchaseDayXPlnCurrencyRate = BigDecimal.ONE;
            } else {
                purchaseDayXPlnCurrencyRate = getFirstAvailableNbpData(symbolCurrency, purchaseDate).getRates().get(0).getMid();
            }

            BigDecimal todayXPlnCurrencyRate;
            switch (symbolCurrency) {
                case "USD" -> todayXPlnCurrencyRate = todayUsdPlnCurrencyRate;
                case "CAD" -> todayXPlnCurrencyRate = todayCadPlnCurrencyRate;
                case "GBP" -> todayXPlnCurrencyRate = todayGbpPlnCurrencyRate;
                case "PLN" -> todayXPlnCurrencyRate = BigDecimal.ONE;
                default -> throw new IllegalArgumentException("Unmapped currency: " + symbolCurrency);
            }

            BigDecimal lastPrice;

            //many stocks on London Stock Exchange trade against GBX/GBp, which is equivalent to 0.01 GBP, therefore division is required
            if (os.getCurrency().equalsIgnoreCase("GBP")) {
                lastPrice = yahooFinanceService.getSimplifiedData(ticker).getMeta().getLastPrice().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                lastPrice = yahooFinanceService.getSimplifiedData(ticker).getMeta().getLastPrice();
            }

            BigDecimal purchaseDayValue = calculateValue(os.getPurchasePrice(), purchaseDayXPlnCurrencyRate, os.getPosition());
            BigDecimal todayValue = calculateValue(lastPrice, todayXPlnCurrencyRate, os.getPosition());

            BigDecimal priceChange = todayValue.subtract(purchaseDayValue);

            ProfitReportDto reportDataRow = ProfitReportDto.builder()
                    .name(os.getName())
                    .ticker(ticker)
                    .purchaseDate(LocalDate.parse(purchaseDate))
                    .purchaseCurrencyRateToPLN(purchaseDayXPlnCurrencyRate)
                    .todayCurrencyRateToPLN(todayXPlnCurrencyRate)
                    .singleShare(ProfitReportDto.SingleShare.builder()
                            .purchasePrice(new ProfitReportDto.PriceWithCurrency(os.getPurchasePrice(), os.getCurrency()))
                            .purchasePriceConverted(new ProfitReportDto.PriceWithCurrency(os.getPurchasePrice().multiply(purchaseDayXPlnCurrencyRate).setScale(2, RoundingMode.HALF_UP), "PLN"))
                            .todayPrice(new ProfitReportDto.PriceWithCurrency(lastPrice, os.getCurrency()))
                            .todayPriceConverted(new ProfitReportDto.PriceWithCurrency(lastPrice.multiply(todayXPlnCurrencyRate).setScale(2, RoundingMode.HALF_UP), "PLN"))
                            .diff(new ProfitReportDto.PriceWithCurrency(lastPrice.subtract(os.getPurchasePrice()), os.getCurrency()))
                            .diffConverted(new ProfitReportDto.PriceWithCurrency(priceChange, "PLN"))
                            .build())
                    .totalShares(ProfitReportDto.TotalShares.builder()
                            .amount(os.getPosition())
                            .purchasePrice(new ProfitReportDto.PriceWithCurrency(calculateValue(os.getPurchasePrice(), BigDecimal.ONE, os.getPosition()), os.getCurrency()))
                            .purchasePriceConverted(new ProfitReportDto.PriceWithCurrency(purchaseDayValue, "PLN"))
                            .todayPrice(new ProfitReportDto.PriceWithCurrency(calculateValue(lastPrice, BigDecimal.ONE, os.getPosition()), os.getCurrency()))
                            .todayPriceConverted(new ProfitReportDto.PriceWithCurrency(todayValue, "PLN"))
                            .diff(new ProfitReportDto.PriceWithCurrency(calculateValue(lastPrice, BigDecimal.ONE, os.getPosition()).subtract(calculateValue(os.getPurchasePrice(), BigDecimal.ONE, os.getPosition())), os.getCurrency()))
                            .diffConverted(new ProfitReportDto.PriceWithCurrency(priceChange, "PLN"))
                            .diffPercentage(todayValue.subtract(purchaseDayValue).divide(purchaseDayValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)))
                            .build())
                    .build();

            results.add(reportDataRow);

            Utils.sleep(100); //to avoid spamming NBP & Yahoo Finance APIs too much
        }

        results.forEach(result -> log.info("{} purchase price: {}, today price: {}, profit/loss: {}",
                result.getName(), result.getTotalShares().purchasePrice(), result.getTotalShares().todayPrice(), result.getTotalShares().diff()));

        log.info("FINISH: Preparing owned stock profit report");

        return results;
    }
}