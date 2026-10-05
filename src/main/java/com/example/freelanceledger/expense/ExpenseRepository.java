package com.example.freelanceledger.expense;

import com.example.freelanceledger.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserOrderBySpentOnDesc(User user);

    Optional<Expense> findByIdAndUser(Long id, User user);
}
