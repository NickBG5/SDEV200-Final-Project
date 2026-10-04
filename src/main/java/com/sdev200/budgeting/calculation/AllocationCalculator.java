package com.sdev200.budgeting.calculation;

import java.math.BigDecimal;

public interface AllocationCalculator {

    Allocation calculate(BigDecimal monthlyIncome);

    record Allocation(BigDecimal needs, BigDecimal wants, BigDecimal savings) {
    }
}
