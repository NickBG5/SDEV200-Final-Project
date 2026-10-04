package com.sdev200.budgeting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BudgetCategory category;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Expense() {
    }

    public Expense(Budget budget, String description, BigDecimal amount, BudgetCategory category) {
        this.budget = budget;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    @PrePersist
    void setCreatedAt() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Budget getBudget() {
        return budget;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BudgetCategory getCategory() {
        return category;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
