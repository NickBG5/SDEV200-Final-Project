package com.sdev200.budgeting.service;

import com.sdev200.budgeting.calculation.AllocationCalculator;
import com.sdev200.budgeting.model.AppUser;
import com.sdev200.budgeting.model.Budget;
import com.sdev200.budgeting.model.BudgetCategory;
import com.sdev200.budgeting.model.Expense;
import com.sdev200.budgeting.repository.BudgetRepository;
import com.sdev200.budgeting.repository.ExpenseRepository;
import com.sdev200.budgeting.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final AllocationCalculator allocationCalculator;

    public BudgetService(UserRepository userRepository, BudgetRepository budgetRepository,
                         ExpenseRepository expenseRepository, AllocationCalculator allocationCalculator) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.allocationCalculator = allocationCalculator;
    }

    @Transactional
    public BudgetResponse create(Long userId, String month, BigDecimal monthlyIncome) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " was not found."));
        if (budgetRepository.existsByUser_IdAndMonth(userId, month)) {
            throw new ConflictException("A budget already exists for " + month + ".");
        }
        Budget budget = budgetRepository.save(new Budget(user, month, monthlyIncome));
        return summarize(budget, Map.of());
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> list(Long userId) {
        requireUser(userId);
        List<Budget> budgets = budgetRepository.findAllByUser_IdOrderByMonthDesc(userId);
        if (budgets.isEmpty()) {
            return List.of();
        }
        List<Long> budgetIds = budgets.stream().map(Budget::getId).toList();
        Map<Long, Map<BudgetCategory, BigDecimal>> spendingByBudget = new java.util.HashMap<>();
        for (Expense expense : expenseRepository.findAllByBudget_IdIn(budgetIds)) {
            spendingByBudget.computeIfAbsent(expense.getBudget().getId(), ignored -> new EnumMap<>(BudgetCategory.class))
                    .merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
        }
        return budgets.stream()
                .map(budget -> summarize(budget, spendingByBudget.getOrDefault(budget.getId(), Map.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponse get(Long userId, Long budgetId) {
        Budget budget = findBudget(userId, budgetId);
        return summarize(budget, spendingFor(budgetId));
    }

    @Transactional
    public ExpenseResponse addExpense(Long userId, Long budgetId, String description,
                                      BigDecimal amount, BudgetCategory category) {
        Budget budget = findBudget(userId, budgetId);
        Expense expense = expenseRepository.save(
                new Expense(budget, description.trim(), amount, category));
        return toExpenseResponse(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> listExpenses(Long userId, Long budgetId) {
        findBudget(userId, budgetId);
        return expenseRepository.findAllByBudget_IdOrderByCreatedAtAsc(budgetId).stream()
                .map(this::toExpenseResponse)
                .toList();
    }

    private AppUser requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " was not found."));
    }

    private Budget findBudget(Long userId, Long budgetId) {
        return budgetRepository.findByIdAndUser_Id(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget " + budgetId + " was not found for this user."));
    }

    private Map<BudgetCategory, BigDecimal> spendingFor(Long budgetId) {
        Map<BudgetCategory, BigDecimal> spending = new EnumMap<>(BudgetCategory.class);
        expenseRepository.sumByCategoryForBudget(budgetId)
                .forEach(row -> spending.put(row.getCategory(), row.getTotal()));
        return spending;
    }

    private BudgetResponse summarize(Budget budget, Map<BudgetCategory, BigDecimal> spending) {
        AllocationCalculator.Allocation allocation = allocationCalculator.calculate(budget.getMonthlyIncome());
        List<CategorySummary> categories = new ArrayList<>();
        categories.add(categorySummary(BudgetCategory.NEEDS, allocation.needs(), spending));
        categories.add(categorySummary(BudgetCategory.WANTS, allocation.wants(), spending));
        categories.add(categorySummary(BudgetCategory.SAVINGS, allocation.savings(), spending));
        return new BudgetResponse(budget.getId(), budget.getUser().getId(), budget.getMonth(),
                budget.getMonthlyIncome(), categories);
    }

    private CategorySummary categorySummary(BudgetCategory category, BigDecimal allocated,
                                            Map<BudgetCategory, BigDecimal> spending) {
        BigDecimal spent = spending.getOrDefault(category, BigDecimal.ZERO.setScale(2));
        return new CategorySummary(category, allocated, spent, allocated.subtract(spent));
    }

    private ExpenseResponse toExpenseResponse(Expense expense) {
        return new ExpenseResponse(expense.getId(), expense.getDescription(), expense.getAmount(),
                expense.getCategory(), expense.getCreatedAt());
    }

    public record BudgetResponse(Long id, Long userId, String month, BigDecimal monthlyIncome,
                                 List<CategorySummary> categories) {
    }

    public record CategorySummary(BudgetCategory category, BigDecimal allocated,
                                  BigDecimal spent, BigDecimal remaining) {
    }

    public record ExpenseResponse(Long id, String description, BigDecimal amount,
                                  BudgetCategory category, Instant createdAt) {
    }
}
