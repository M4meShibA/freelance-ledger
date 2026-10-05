package com.example.freelanceledger.expense;

import com.example.freelanceledger.common.exception.ResourceNotFoundException;
import com.example.freelanceledger.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseCategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Expense> findAllForUser(User user) {
        return expenseRepository.findByUserOrderBySpentOnDesc(user);
    }

    @Transactional(readOnly = true)
    public Expense findByIdForUser(Long id, User user) {
        return expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("経費データが見つかりません: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<ExpenseCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    private ExpenseCategory findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("勘定科目が見つかりません: id=" + categoryId));
    }

    @Transactional
    public Expense create(User user, Long categoryId, String description, BigDecimal amount, LocalDate spentOn) {
        ExpenseCategory category = findCategory(categoryId);
        Expense expense = new Expense(user, category, description, amount, spentOn);
        return expenseRepository.save(expense);
    }

    @Transactional
    public Expense update(Long id, User user, Long categoryId, String description, BigDecimal amount, LocalDate spentOn) {
        Expense expense = findByIdForUser(id, user);
        ExpenseCategory category = findCategory(categoryId);
        expense.update(category, description, amount, spentOn);
        return expense; // JPAのダーティチェックで更新される
    }

    @Transactional
    public void delete(Long id, User user) {
        Expense expense = findByIdForUser(id, user);
        expenseRepository.delete(expense);
    }
}
