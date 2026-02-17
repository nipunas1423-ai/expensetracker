package com.example.expensetracker.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // ---------------- CRUD ----------------

    @Override
    public Expense saveExpense(Expense expense, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getAllExpensesByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUser(user);
    }

    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
    }

    @Override
    public Expense updateExpense(Long id, Expense expense) {
        Expense existingExpense = getExpenseById(id);
        existingExpense.setTitle(expense.getTitle());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setAmount(expense.getAmount());
        existingExpense.setDate(expense.getDate());
        return expenseRepository.save(existingExpense);
    }

    @Override
    public void deleteExpense(Long id) {
        Expense expense = getExpenseById(id);
        expenseRepository.delete(expense);
    }

    // ---------------- FILTERING ----------------

    @Override
    public List<Expense> getExpensesByCategoryAndUser(String category, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByCategoryAndUser(category, user);
    }

   @Override
public List<Expense> getExpensesByDateRangeAndUser(LocalDate start, LocalDate end, String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return expenseRepository.findByDateBetweenAndUser(start, end, user);
}


    // ---------------- PAGINATION ----------------

    @Override
    public Page<Expense> getAllExpenses(int page, int size) {
        return expenseRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Page<Expense> getExpensesPagedAndSorted(int page, int size, String sortBy, String direction) {
        Sort sort = direction != null && direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return expenseRepository.findAll(PageRequest.of(page, size, sort));
    }

    // ---------------- BUSINESS LOGIC ----------------

    @Override
    public Map<String, Double> getExpenseSummaryByCategory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findByUser(user);
        Map<String, Double> summary = new HashMap<>();

        for (Expense expense : expenses) {
            summary.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        return summary;
    }

    @Override
    public Map<Integer, Double> getMonthlyExpenseSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findByUser(user);
        Map<Integer, Double> summary = new HashMap<>();

        for (Expense expense : expenses) {
            int month = expense.getDate().getMonthValue();
            summary.merge(month, expense.getAmount(), Double::sum);
        }

        return summary;
    }
}
