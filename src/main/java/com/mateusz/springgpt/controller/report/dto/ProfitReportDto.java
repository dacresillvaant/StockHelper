package com.mateusz.springgpt.controller.report.dto;

import java.math.BigDecimal;

public record ProfitReportDto(String name, String ticker, BigDecimal purchasePrice, BigDecimal todayPrice, BigDecimal diff) {}