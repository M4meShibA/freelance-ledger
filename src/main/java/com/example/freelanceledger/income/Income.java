package com.example.freelanceledger.income;

import com.example.freelanceledger.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "incomes")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String clientName;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDate receivedOn;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Income() {
        // JPA用
    }

    public Income(User user, String clientName, String description, BigDecimal amount, LocalDate receivedOn) {
        this.user = user;
        this.clientName = clientName;
        this.description = description;
        this.amount = amount;
        this.receivedOn = receivedOn;
    }

    public void update(String clientName, String description, BigDecimal amount, LocalDate receivedOn) {
        this.clientName = clientName;
        this.description = description;
        this.amount = amount;
        this.receivedOn = receivedOn;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getReceivedOn() {
        return receivedOn;
    }
}
