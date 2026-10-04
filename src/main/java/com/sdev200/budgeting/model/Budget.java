package com.sdev200.budgeting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity
@Table(name = "budgets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_budget_user_month", columnNames = {"user_id", "budget_month"})
})
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "budget_month", nullable = false, length = 7)
    private String month;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    protected Budget() {
    }

    public Budget(AppUser user, String month, BigDecimal monthlyIncome) {
        this.user = user;
        this.month = month;
        this.monthlyIncome = monthlyIncome;
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }
}
