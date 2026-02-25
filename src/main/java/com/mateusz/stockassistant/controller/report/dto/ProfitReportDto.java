package com.mateusz.stockassistant.controller.report.dto;

import java.math.BigDecimal;

public record ProfitReportDto(String name, String ticker, BigDecimal purchasePrice, BigDecimal todayPrice, BigDecimal diff) {}