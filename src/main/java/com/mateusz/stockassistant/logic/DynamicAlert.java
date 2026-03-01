package com.mateusz.stockassistant.logic;

import com.mateusz.stockassistant.controller.twelvedata.dto.QuoteExternalDto;
import com.mateusz.stockassistant.controller.yahoofinance.dto.YahooDetailedChartResponseDto;
import com.mateusz.stockassistant.entity.AlertConfigEntity;
import com.mateusz.stockassistant.entity.OwnedStockEntity;
import com.mateusz.stockassistant.service.*;
import com.mateusz.stockassistant.tools.Utils;
import com.mateusz.stockassistant.tools.mail.MailTemplate;
import com.mateusz.stockassistant.tools.mail.MailTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mateusz.stockassistant.controller.alertconfig.AlertType.INDEX_ALERT;
import static com.mateusz.stockassistant.controller.alertconfig.AlertType.LOW_PRICE_ALERT;

@Component
@Slf4j
public class DynamicAlert {

    private final TwelveDataService twelveDataService;
    private final YahooFinanceService yahooFinanceService;
    private final MailgunEmailService mailgunEmailService;
    private final StockService stockService;
    private final AlertConfigService alertConfigService;

    @Value("${mailgun.default-receiver}")
    private String mailReceiver;

    @Autowired
    public DynamicAlert(TwelveDataService twelveDataService, MailgunEmailService mailgunEmailService,
                        StockService stockService, AlertConfigService alertConfigService, YahooFinanceService yahooFinanceService) {
        this.twelveDataService = twelveDataService;
        this.mailgunEmailService = mailgunEmailService;
        this.stockService = stockService;
        this.alertConfigService = alertConfigService;
        this.yahooFinanceService = yahooFinanceService;
    }

    public void lowPriceAlert() {
        List<AlertConfigEntity> listOfAlertConfig = alertConfigService.getAlertConfigurationsByAlertType(LOW_PRICE_ALERT);

        for (AlertConfigEntity alertConfig : listOfAlertConfig) {
            String symbol = alertConfig.getTicker();
            int percentChange = alertConfig.getPercentChangeThreshold();

            QuoteExternalDto quote = twelveDataService.getQuote(symbol).getBody();
            BigDecimal lastClose = new BigDecimal(quote.getClose());
            BigDecimal yearHigh = new BigDecimal(quote.getFiftyTwoWeek().getHigh());

            BigDecimal percentThreshold = new BigDecimal(100 - percentChange).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal alertThreshold = yearHigh.multiply(percentThreshold);

            log.info("Symbol: \"{}\" - last close price: {}, year high: {}, threshold for alert: {}", symbol, lastClose, yearHigh, alertThreshold);

            if (lastClose.compareTo(alertThreshold) < 0) {
                MailTemplate mailTemplate = MailTemplateFactory.lowPriceAlertTemplate(symbol, percentChange, quote, lastClose, yearHigh, alertThreshold);
                mailgunEmailService.sendEmail(mailReceiver, mailTemplate);
            }
        }
    }

    private void compareOwnedStocksPrice(List<OwnedStockEntity> ownedStocks, Map<String, QuoteExternalDto> ownedStocksLatestData, int percentChangeThreshold) {

        for (OwnedStockEntity ownedStock : ownedStocks) {
            String symbol = ownedStock.getTicker();
            BigDecimal purchasePrice = ownedStock.getPurchasePrice();
            BigDecimal lastClosePrice = new BigDecimal(ownedStocksLatestData.get(symbol).getClose());
            BigDecimal priceChange = Utils.calculatePercentChange(purchasePrice, lastClosePrice);

            if (priceChange.abs().compareTo(BigDecimal.valueOf(percentChangeThreshold)) > 0) {
                MailTemplate mailTemplate = MailTemplateFactory.ownedStockPriceAlertTemplate(ownedStock, purchasePrice, lastClosePrice, priceChange, percentChangeThreshold);
                mailgunEmailService.sendEmail(mailReceiver, mailTemplate);
            }

            log.info("Symbol: {}, purchase price: {}, last close price: {}, price change: {}%", symbol, purchasePrice, lastClosePrice, priceChange);
        }
    }

    public void ownedStockPriceAlert(int percentChangeThreshold) {
        int batchSize = 8;

        List<OwnedStockEntity> ownedStocks = stockService.getAllStocks();
        Map<String, QuoteExternalDto> ownedStocksCurrentData = new HashMap<>();
        ownedStocks.forEach(s -> log.info("Found stock ticker: " + s.getName()));

        for (int i = 0; i < ownedStocks.size(); i+= batchSize) {
            log.info("Starting batch");
            List<OwnedStockEntity> batchOfStocks = new ArrayList<>(ownedStocks.subList(i, Math.min(i + batchSize, ownedStocks.size())));
            batchOfStocks.forEach(s -> ownedStocksCurrentData.put(s.getTicker(), twelveDataService.getQuote(s.getTicker()).getBody()));

            compareOwnedStocksPrice(batchOfStocks, ownedStocksCurrentData, percentChangeThreshold);

            // Sleep only if it's not the last batch
            if (i + batchSize < ownedStocks.size()) {
                Utils.sleep(65000);
            } else {
                log.info("Finished processing last batch.");
            }
        }
    }

    public void indexVolatilityAlert() {
        List<AlertConfigEntity> listOfAlertConfig = alertConfigService.getAlertConfigurationsByAlertType(INDEX_ALERT);

        for  (AlertConfigEntity alertConfig : listOfAlertConfig) {
            String symbol = alertConfig.getTicker();
            int percentChange = alertConfig.getPercentChangeThreshold();
            YahooDetailedChartResponseDto previousData = yahooFinanceService.getDetailedData(symbol, "5d", "1d");
        }
    }
}