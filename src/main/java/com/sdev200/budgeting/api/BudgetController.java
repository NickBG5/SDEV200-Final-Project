package com.sdev200.budgeting.api;

import com.sdev200.budgeting.model.BudgetCategory;
import com.sdev200.budgeting.service.BudgetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetService.BudgetResponse create(@PathVariable Long userId,
                                               @Valid @RequestBody CreateBudgetRequest request) {
        return budgetService.create(userId, request.month(), request.monthlyIncome());
    }

    @GetMapping
    public List<BudgetService.BudgetResponse> list(@PathVariable Long userId) {
        return budgetService.list(userId);
    }

    @GetMapping("/{budgetId}")
    public BudgetService.BudgetResponse get(@PathVariable Long userId, @PathVariable Long budgetId) {
        return budgetService.get(userId, budgetId);
    }

    @PostMapping("/{budgetId}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetService.ExpenseResponse addExpense(@PathVariable Long userId, @PathVariable Long budgetId,
                                                    @Valid @RequestBody CreateExpenseRequest request) {
        return budgetService.addExpense(userId, budgetId, request.description(), request.amount(), request.category());
    }

    @GetMapping("/{budgetId}/expenses")
    public List<BudgetService.ExpenseResponse> listExpenses(@PathVariable Long userId,
                                                            @PathVariable Long budgetId) {
        return budgetService.listExpenses(userId, budgetId);
    }

    public record CreateBudgetRequest(
            @NotBlank @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "month must use YYYY-MM format")
            String month,
            @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal monthlyIncome) {
    }

    public record CreateExpenseRequest(
            @NotBlank @Size(max = 200) String description,
            @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal amount,
            @NotNull BudgetCategory category) {
    }
}
