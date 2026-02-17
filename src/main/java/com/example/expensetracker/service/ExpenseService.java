package com.example.expensetracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.expensetracker.entity.Expense;

public interface ExpenseService {

    // Save expense
    Expense saveExpense(Expense expense, String email);

    // Get all expenses for a user
    List<Expense> getAllExpensesByUser(String email);

    // Get all expenses (admin / global)
    List<Expense> getAllExpenses();

    // Get expense by ID
    Expense getExpenseById(Long id);

    // Update expense
    Expense updateExpense(Long id, Expense expense);

    // Delete expense
    void deleteExpense(Long id);

    // Filter by category for user
    List<Expense> getExpensesByCategoryAndUser(String category, String email);

    // Filter by date range for user
    List<Expense> getExpensesByDateRangeAndUser(LocalDate start, LocalDate end, String email);

    // Business logic
    Map<String, Double> getExpenseSummaryByCategory(String email);

    Map<Integer, Double> getMonthlyExpenseSummary(String email);

    // Optional: pagination
    Page<Expense> getAllExpenses(int page, int size);

    Page<Expense> getExpensesPagedAndSorted(int page, int size, String sortBy, String direction);
}
