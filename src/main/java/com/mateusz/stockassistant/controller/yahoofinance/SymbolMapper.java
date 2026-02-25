package com.mateusz.stockassistant.controller.yahoofinance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SymbolMapper {

    USDPLN("PLN=X"),
    CADPLN("CADPLN=X"),
    EURPLN("EURPLN=X");

    private final String yahooValue;
}