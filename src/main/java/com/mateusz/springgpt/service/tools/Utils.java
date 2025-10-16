package com.mateusz.springgpt.service.tools;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static BigDecimal calculatePercentChange(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice == null || newPrice == null) {
            throw new IllegalArgumentException("Prices cannot be null");
        }

        if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot calculate percentage change from zero oldPrice");
        }

        BigDecimal change = newPrice.subtract(oldPrice);
        return change.divide(oldPrice, 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}