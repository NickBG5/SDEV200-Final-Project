package com.sdev200.budgeting.repository;

import com.sdev200.budgeting.model.BudgetCategory;
import com.sdev200.budgeting.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByBudget_IdOrderByCreatedAtAsc(Long budgetId);

    List<Expense> findAllByBudget_IdIn(List<Long> budgetIds);

    interface CategorySpending {
        BudgetCategory getCategory();

        BigDecimal getTotal();
    }

    @org.springframework.data.jpa.repository.Query("""
            select e.category as category, sum(e.amount) as total
            from Expense e
            where e.budget.id = :budgetId
            group by e.category
            """)
    List<CategorySpending> sumByCategoryForBudget(Long budgetId);
}
