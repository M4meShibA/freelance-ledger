package com.example.freelanceledger.summary;

import java.math.BigDecimal;

/** 所得税・住民税・国民健康保険料の概算結果(あくまで簡易試算) */
public class TaxEstimate {

    private final BigDecimal grossIncome;      // 年間の収入合計
    private final BigDecimal totalExpense;     // 年間の経費合計
    private final BigDecimal businessProfit;   // 収入 - 経費(青色申告特別控除前)
    private final BigDecimal taxableIncome;    // 課税所得(各種控除後の概算)
    private final BigDecimal incomeTax;        // 所得税(概算)
    private final BigDecimal residentTax;      // 住民税(概算)
    private final BigDecimal nationalHealthInsurance; // 国民健康保険料(概算)

    public TaxEstimate(BigDecimal grossIncome, BigDecimal totalExpense, BigDecimal businessProfit,
                        BigDecimal taxableIncome, BigDecimal incomeTax, BigDecimal residentTax,
                        BigDecimal nationalHealthInsurance) {
        this.grossIncome = grossIncome;
        this.totalExpense = totalExpense;
        this.businessProfit = businessProfit;
        this.taxableIncome = taxableIncome;
        this.incomeTax = incomeTax;
        this.residentTax = residentTax;
        this.nationalHealthInsurance = nationalHealthInsurance;
    }

    public BigDecimal getGrossIncome() { return grossIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public BigDecimal getBusinessProfit() { return businessProfit; }
    public BigDecimal getTaxableIncome() { return taxableIncome; }
    public BigDecimal getIncomeTax() { return incomeTax; }
    public BigDecimal getResidentTax() { return residentTax; }
    public BigDecimal getNationalHealthInsurance() { return nationalHealthInsurance; }

    public BigDecimal getTotalTaxAndInsurance() {
        return incomeTax.add(residentTax).add(nationalHealthInsurance);
    }

    public BigDecimal getEstimatedNetProfit() {
        return businessProfit.subtract(getTotalTaxAndInsurance());
    }
}
