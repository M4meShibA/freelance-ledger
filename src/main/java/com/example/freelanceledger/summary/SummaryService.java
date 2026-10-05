package com.example.freelanceledger.summary;

import com.example.freelanceledger.expense.Expense;
import com.example.freelanceledger.expense.ExpenseRepository;
import com.example.freelanceledger.income.Income;
import com.example.freelanceledger.income.IncomeRepository;
import com.example.freelanceledger.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 月別・年別の収支集計を行うサービス。
 *
 * 実装メモ(面接で聞かれたら):
 * 現在は全件をメモリに読み込んでStreamでフィルタしている。
 * データ量が増えるとパフォーマンスが落ちるため、本来はDBの集計クエリ
 * (SUM + GROUP BY YearMonth)に置き換えるのが望ましい。
 * v1では実装のシンプルさを優先し、この点は今後の改善点としている。
 */
@Service
public class SummaryService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public SummaryService(IncomeRepository incomeRepository, ExpenseRepository expenseRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public MonthlySummary getMonthlySummary(User user, YearMonth month) {
        BigDecimal income = incomeRepository.findByUserOrderByReceivedOnDesc(user).stream()
                .filter(i -> YearMonth.from(i.getReceivedOn()).equals(month))
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = expenseRepository.findByUserOrderBySpentOnDesc(user).stream()
                .filter(e -> YearMonth.from(e.getSpentOn()).equals(month))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlySummary(month, income, expense);
    }

    @Transactional(readOnly = true)
    public YearlySummary getYearlySummary(User user, int year) {
        List<Income> incomes = incomeRepository.findByUserOrderByReceivedOnDesc(user);
        List<Expense> expenses = expenseRepository.findByUserOrderBySpentOnDesc(user);

        List<MonthlySummary> months = new ArrayList<>();
        for (Month m : Month.values()) {
            YearMonth ym = YearMonth.of(year, m);

            BigDecimal income = incomes.stream()
                    .filter(i -> YearMonth.from(i.getReceivedOn()).equals(ym))
                    .map(Income::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expense = expenses.stream()
                    .filter(e -> YearMonth.from(e.getSpentOn()).equals(ym))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            months.add(new MonthlySummary(ym, income, expense));
        }
        return new YearlySummary(year, months);
    }
}
