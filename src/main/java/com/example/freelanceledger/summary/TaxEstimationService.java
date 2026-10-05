package com.example.freelanceledger.summary;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 所得税・住民税・国民健康保険料の「概算」を計算するサービス。
 *
 * 重要: これは学習・ポートフォリオ用の簡易試算であり、実際の税額を
 * 保証するものではない。控除の種類(社会保険料控除、扶養控除など)は
 * 簡略化しており、正確な金額は税理士や確定申告ソフトで確認すること。
 *
 * 前提にしている簡易ルール:
 * - 青色申告特別控除: 65万円(電子申告を行う場合)を一律で適用
 * - 基礎控除: 48万円(所得税)/ 43万円(住民税) ※所得2,400万円以下の場合
 * - 所得税: 国税庁の速算表(2024年分の税率・控除額)を使用
 * - 住民税: 所得割 10%(市区町村民税6% + 道府県民税4%)の概算のみ
 *   (均等割は含めていない)
 * - 国民健康保険料: 所得割を概算10%として計算(自治体により大きく異なる)
 */
@Service
public class TaxEstimationService {

    private static final BigDecimal BLUE_RETURN_DEDUCTION = new BigDecimal("650000");
    private static final BigDecimal INCOME_TAX_BASIC_DEDUCTION = new BigDecimal("480000");
    private static final BigDecimal RESIDENT_TAX_BASIC_DEDUCTION = new BigDecimal("430000");
    private static final BigDecimal RESIDENT_TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal NATIONAL_HEALTH_INSURANCE_RATE = new BigDecimal("0.10");

    public TaxEstimate estimate(BigDecimal grossIncome, BigDecimal totalExpense) {
        BigDecimal businessProfit = grossIncome.subtract(totalExpense).max(BigDecimal.ZERO);

        // 所得税の課税所得 = 事業所得 - 青色申告特別控除 - 基礎控除
        BigDecimal incomeTaxableIncome = businessProfit
                .subtract(BLUE_RETURN_DEDUCTION)
                .subtract(INCOME_TAX_BASIC_DEDUCTION)
                .max(BigDecimal.ZERO);

        BigDecimal incomeTax = calculateIncomeTax(incomeTaxableIncome);

        // 住民税の課税所得 = 事業所得 - 青色申告特別控除 - 基礎控除(住民税用)
        BigDecimal residentTaxableIncome = businessProfit
                .subtract(BLUE_RETURN_DEDUCTION)
                .subtract(RESIDENT_TAX_BASIC_DEDUCTION)
                .max(BigDecimal.ZERO);

        BigDecimal residentTax = residentTaxableIncome.multiply(RESIDENT_TAX_RATE)
                .setScale(0, RoundingMode.FLOOR);

        // 国民健康保険料(所得割のみの概算。均等割・平等割は含めていない)
        BigDecimal insuranceBase = businessProfit.subtract(BLUE_RETURN_DEDUCTION).max(BigDecimal.ZERO);
        BigDecimal nationalHealthInsurance = insuranceBase.multiply(NATIONAL_HEALTH_INSURANCE_RATE)
                .setScale(0, RoundingMode.FLOOR);

        return new TaxEstimate(grossIncome, totalExpense, businessProfit, incomeTaxableIncome,
                incomeTax, residentTax, nationalHealthInsurance);
    }

    /**
     * 所得税の速算表による概算計算(2024年分の税率区分を使用)。
     * 課税所得(千円未満切り捨て)に税率を掛け、控除額を引く方式。
     */
    private BigDecimal calculateIncomeTax(BigDecimal taxableIncome) {
        BigDecimal base = taxableIncome.setScale(-3, RoundingMode.FLOOR); // 千円未満切り捨て
        long income = base.longValue();

        BigDecimal rate;
        BigDecimal deduction;

        if (income <= 1_950_000) {
            rate = new BigDecimal("0.05");
            deduction = BigDecimal.ZERO;
        } else if (income <= 3_300_000) {
            rate = new BigDecimal("0.10");
            deduction = new BigDecimal("97500");
        } else if (income <= 6_950_000) {
            rate = new BigDecimal("0.20");
            deduction = new BigDecimal("427500");
        } else if (income <= 9_000_000) {
            rate = new BigDecimal("0.23");
            deduction = new BigDecimal("636000");
        } else if (income <= 18_000_000) {
            rate = new BigDecimal("0.33");
            deduction = new BigDecimal("1536000");
        } else if (income <= 40_000_000) {
            rate = new BigDecimal("0.40");
            deduction = new BigDecimal("2796000");
        } else {
            rate = new BigDecimal("0.45");
            deduction = new BigDecimal("4796000");
        }

        BigDecimal tax = base.multiply(rate).subtract(deduction).max(BigDecimal.ZERO);
        return tax.setScale(0, RoundingMode.FLOOR);
    }
}
