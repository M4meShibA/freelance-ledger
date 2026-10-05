package com.example.freelanceledger.summary;

import java.math.BigDecimal;
import java.time.YearMonth;

/** ある月の収入合計・経費合計・差引を表す集計結果 */
public class MonthlySummary {

    private final YearMonth month;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;

    public MonthlySummary(YearMonth month, BigDecimal totalIncome, BigDecimal totalExpense) {
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getProfit() {
        return totalIncome.subtract(totalExpense);
    }
}
