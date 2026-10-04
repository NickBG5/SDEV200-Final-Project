package com.sdev200.budgeting.repository;

import com.sdev200.budgeting.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    boolean existsByUser_IdAndMonth(Long userId, String month);

    List<Budget> findAllByUser_IdOrderByMonthDesc(Long userId);

    Optional<Budget> findByIdAndUser_Id(Long budgetId, Long userId);
}
