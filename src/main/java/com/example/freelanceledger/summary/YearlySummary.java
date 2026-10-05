package com.example.freelanceledger.summary;

import java.math.BigDecimal;
import java.util.List;

/** ある年の、月別内訳と年間合計をまとめた集計結果 */
public class YearlySummary {

    private final int year;
    private final List<MonthlySummary> months;

    public YearlySummary(int year, List<MonthlySummary> months) {
        this.year = year;
        this.months = months;
    }

    public int getYear() {
        return year;
    }

    public List<MonthlySummary> getMonths() {
        return months;
    }

    public BigDecimal getTotalIncome() {
        return months.stream().map(MonthlySummary::getTotalIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense() {
        return months.stream().map(MonthlySummary::getTotalExpense).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalProfit() {
        return getTotalIncome().subtract(getTotalExpense());
    }
}
