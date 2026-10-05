package com.example.freelanceledger.expense;

import com.example.freelanceledger.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory category;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDate spentOn;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Expense() {
        // JPA用
    }

    public Expense(User user, ExpenseCategory category, String description, BigDecimal amount, LocalDate spentOn) {
        this.user = user;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.spentOn = spentOn;
    }

    public void update(ExpenseCategory category, String description, BigDecimal amount, LocalDate spentOn) {
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.spentOn = spentOn;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getSpentOn() {
        return spentOn;
    }
}
