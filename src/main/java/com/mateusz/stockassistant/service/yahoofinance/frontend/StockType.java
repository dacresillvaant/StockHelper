package com.mateusz.stockassistant.service.yahoofinance.frontend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockType {
    MOST_ACTIVE("most-active"),
    TRENDING("trending"),
    HIGHEST_DIVIDEND("highest-dividend"),
    LARGE_CAP_STOCKS("large-cap-stocks"),
    UNUSUAL_VOLUME_STOCKS("unusual-volume-stocks");

    private final String description;
}