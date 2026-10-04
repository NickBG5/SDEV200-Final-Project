package com.sdev200.budgeting.calculation;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FiftyThirtyTwentyCalculator implements AllocationCalculator {

    private static final BigDecimal NEEDS_RATE = new BigDecimal("0.50");
    private static final BigDecimal WANTS_RATE = new BigDecimal("0.30");

    @Override
    public Allocation calculate(BigDecimal monthlyIncome) {
        BigDecimal needs = monthlyIncome.multiply(NEEDS_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal wants = monthlyIncome.multiply(WANTS_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal savings = monthlyIncome.subtract(needs).subtract(wants);
        return new Allocation(needs, wants, savings);
    }
}
