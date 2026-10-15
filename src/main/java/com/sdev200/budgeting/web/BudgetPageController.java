package com.sdev200.budgeting.web;

import com.sdev200.budgeting.model.BudgetCategory;
import com.sdev200.budgeting.service.BudgetService;
import com.sdev200.budgeting.service.ConflictException;
import com.sdev200.budgeting.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BudgetPageController {

    private final UserService userService;
    private final BudgetService budgetService;

    public BudgetPageController(UserService userService, BudgetService budgetService) {
        this.userService = userService;
        this.budgetService = budgetService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "index";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute("userForm") UserForm form,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "index";
        }
        try {
            UserService.UserResponse user = userService.create(form.getName(), form.getEmail());
            return "redirect:/users/" + user.id() + "/budgets";
        } catch (ConflictException exception) {
            model.addAttribute("pageError", exception.getMessage());
            return "index";
        }
    }

    @GetMapping("/users/{userId}/budgets")
    public String budgets(@PathVariable Long userId, Model model) {
        UserService.UserResponse user = userService.get(userId);
        model.addAttribute("user", userMap(user));
        model.addAttribute("budgets", budgetService.list(userId).stream().map(this::budgetMap).toList());
        model.addAttribute("budgetForm", new BudgetForm());
        return "budgets";
    }

    @PostMapping("/users/{userId}/budgets")
    public String createBudget(@PathVariable Long userId,
                              @Valid @ModelAttribute("budgetForm") BudgetForm form,
                              BindingResult bindingResult, Model model) {
        UserService.UserResponse user = userService.get(userId);
        if (bindingResult.hasErrors()) {
            populateBudgets(user, model);
            return "budgets";
        }
        try {
            BudgetService.BudgetResponse budget =
                    budgetService.create(userId, form.getMonth(), form.getMonthlyIncome());
            return "redirect:/users/" + userId + "/budgets/" + budget.id();
        } catch (ConflictException exception) {
            bindingResult.reject("budget.duplicate", exception.getMessage());
            populateBudgets(user, model);
            return "budgets";
        }
    }

    @GetMapping("/users/{userId}/budgets/{budgetId}")
    public String budget(@PathVariable Long userId, @PathVariable Long budgetId, Model model) {
        populateBudgetDetail(userId, budgetId, model);
        model.addAttribute("expenseForm", new ExpenseForm());
        return "budget-detail";
    }

    @PostMapping("/users/{userId}/budgets/{budgetId}/expenses")
    public String addExpense(@PathVariable Long userId, @PathVariable Long budgetId,
                             @Valid @ModelAttribute("expenseForm") ExpenseForm form,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            populateBudgetDetail(userId, budgetId, model);
            return "budget-detail";
        }
        budgetService.addExpense(userId, budgetId, form.getDescription(), form.getAmount(), form.getCategory());
        return "redirect:/users/" + userId + "/budgets/" + budgetId;
    }

    private void populateBudgets(UserService.UserResponse user, Model model) {
        model.addAttribute("user", userMap(user));
        model.addAttribute("budgets", budgetService.list(user.id()).stream().map(this::budgetMap).toList());
        model.addAttribute("categories", categoryOptions());
    }

    private void populateBudgetDetail(Long userId, Long budgetId, Model model) {
        UserService.UserResponse user = userService.get(userId);
        model.addAttribute("user", userMap(user));
        model.addAttribute("budget", budgetMap(budgetService.get(userId, budgetId)));
        model.addAttribute("expenses", budgetService.listExpenses(userId, budgetId).stream()
                .map(this::expenseMap).toList());
        model.addAttribute("categories", categoryOptions());
    }

    private Map<String, Object> userMap(UserService.UserResponse user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.id());
        result.put("name", user.name());
        result.put("email", user.email());
        return result;
    }

    private Map<String, Object> budgetMap(BudgetService.BudgetResponse budget) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", budget.id());
        result.put("month", budget.month());
        result.put("monthlyIncome", budget.monthlyIncome());
        result.put("categories", budget.categories().stream().map(category -> {
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("name", category.category().name());
            summary.put("label", categoryLabel(category.category()));
            summary.put("allocated", category.allocated());
            summary.put("spent", category.spent());
            summary.put("remaining", category.remaining());
            return summary;
        }).toList());
        return result;
    }

    private Map<String, Object> expenseMap(BudgetService.ExpenseResponse expense) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("description", expense.description());
        result.put("amount", expense.amount());
        result.put("category", categoryLabel(expense.category()));
        result.put("createdAt", expense.createdAt());
        return result;
    }

    private List<Map<String, String>> categoryOptions() {
        List<Map<String, String>> options = new ArrayList<>();
        for (BudgetCategory category : BudgetCategory.values()) {
            Map<String, String> option = new LinkedHashMap<>();
            option.put("value", category.name());
            option.put("label", categoryLabel(category));
            options.add(option);
        }
        return options;
    }

    private String categoryLabel(BudgetCategory category) {
        return switch (category) {
            case NEEDS -> "Needs";
            case WANTS -> "Wants";
            case SAVINGS -> "Savings";
        };
    }

    public static class UserForm {
        @NotBlank @Size(max = 100)
        private String name;
        @NotBlank @Email @Size(max = 254)
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class BudgetForm {
        @NotBlank @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "Choose a valid month.")
        private String month = YearMonth.now().toString();
        @NotNull @Positive @Digits(integer = 10, fraction = 2)
        private BigDecimal monthlyIncome;

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    }

    public static class ExpenseForm {
        @NotBlank @Size(max = 200)
        private String description;
        @NotNull @Positive @Digits(integer = 10, fraction = 2)
        private BigDecimal amount;
        @NotNull
        private BudgetCategory category;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BudgetCategory getCategory() { return category; }
        public void setCategory(BudgetCategory category) { this.category = category; }
    }
}
