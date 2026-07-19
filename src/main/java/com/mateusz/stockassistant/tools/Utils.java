package com.mateusz.stockassistant.tools;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static BigDecimal calculatePercentChange(BigDecimal baseValue, BigDecimal comparableValue) {
        if (baseValue == null || comparableValue == null) {
            throw new IllegalArgumentException("Prices cannot be null");
        }

        if (baseValue.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot calculate percentage change from zero baseValue");
        }

        BigDecimal change = comparableValue.subtract(baseValue);
        return change.divide(baseValue, 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}