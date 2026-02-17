package com.example.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // 🔐 Category summary for logged user
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user = :user GROUP BY e.category")
    List<Object[]> getTotalAmountByCategory(User user);

    // 🔐 Monthly summary for logged user
    @Query("SELECT MONTH(e.date), SUM(e.amount) FROM Expense e WHERE e.user = :user GROUP BY MONTH(e.date)")
    List<Object[]> getMonthlyExpenseSummary(User user);

    List<Expense> findByCategoryAndUser(String category, User user);

    List<Expense> findByDateBetweenAndUser(LocalDate startDate, LocalDate endDate, User user);

    List<Expense> findByUser(User user);

List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByCategory(String category);
}



