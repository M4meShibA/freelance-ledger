package com.example.freelanceledger.expense;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * アプリ起動時に、経費データがまだ何も無ければ
 * 基本的な勘定科目をあらかじめ登録しておく。
 */
@Component
public class ExpenseCategorySeeder implements CommandLineRunner {

    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseCategorySeeder(ExpenseCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }
        categoryRepository.save(new ExpenseCategory("通信費", "インターネット、電話代など"));
        categoryRepository.save(new ExpenseCategory("旅費交通費", "電車代、駐車場代など"));
        categoryRepository.save(new ExpenseCategory("消耗品費", "文房具、PC周辺機器など"));
        categoryRepository.save(new ExpenseCategory("地代家賃", "事務所・作業スペースの家賃(按分含む)"));
        categoryRepository.save(new ExpenseCategory("水道光熱費", "電気・ガス・水道代(按分含む)"));
        categoryRepository.save(new ExpenseCategory("外注費", "業務委託・外部発注の支払い"));
        categoryRepository.save(new ExpenseCategory("研修費", "書籍、講座、勉強会の参加費など"));
        categoryRepository.save(new ExpenseCategory("その他", "上記に当てはまらない経費"));
    }
}
