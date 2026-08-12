package com.mateusz.stockassistant.service.yahoofinance.frontend;

import java.math.BigDecimal;

public record MailTemplateData(String ticker, StockType stockType, BigDecimal currentPrice, BigDecimal lowPrice) {}