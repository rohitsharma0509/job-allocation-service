package com.scb.rider.joballocation.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonUtilTest {

    @Test
    void shouldRoundUp() {
        final double VALUE = 10.456;
        final double ROUND_VALUE = 10.46;
        double result = CommonUtils.round(VALUE);
        Assertions.assertEquals(ROUND_VALUE, result);
    }

    @Test
    void shouldRound() {
        final double VALUE = 10.453;
        final double ROUND_VALUE = 10.45;
        double result = CommonUtils.round(VALUE);
        Assertions.assertEquals(ROUND_VALUE, result);
    }
}
