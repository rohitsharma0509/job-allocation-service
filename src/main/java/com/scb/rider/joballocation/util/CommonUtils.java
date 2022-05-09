package com.scb.rider.joballocation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {

    private static final Integer NO_OF_DECIMALS = 2;

    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(NO_OF_DECIMALS, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
