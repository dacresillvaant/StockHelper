package com.mateusz.stockassistant.controller.trend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GeoScope {
    PL("Poland"),
    US("USA"),
    JP("Japan"),
    AU("Australia"),
    DE("Germany");

    private final String fullName;
}