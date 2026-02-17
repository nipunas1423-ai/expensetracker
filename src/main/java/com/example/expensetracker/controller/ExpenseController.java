package com.example.expensetracker.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.service.ExpenseService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // ➕ Add Expense (POST)
    @PostMapping("/save")
    public ResponseEntity<Expense> addExpense(
            @RequestBody Expense expense,
            @RequestParam String email) {

        Expense savedExpense = expenseService.saveExpense(expense, email);
        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    // 📄 Get All Expenses for a user
    @GetMapping("/all")
    public ResponseEntity<List<Expense>> getAllExpenses(@RequestParam String email) {
        return ResponseEntity.ok(expenseService.getAllExpensesByUser(email));
    }

    // 🔍 Get Expense by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    // ✏️ Update Expense
    @PutMapping("/id/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long id,
            @RequestBody Expense expense) {

        return ResponseEntity.ok(expenseService.updateExpense(id, expense));
    }

    // ❌ Delete Expense
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense deleted successfully");
    }

    // 🔍 Filter by Category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(
            @PathVariable String category,
            @RequestParam String email) {

        return ResponseEntity.ok(expenseService.getExpensesByCategoryAndUser(category, email));
    }

    @GetMapping("/dates")
public ResponseEntity<List<Expense>> getExpensesByDateRange(
        @RequestParam String start,
        @RequestParam String end,
        @RequestParam String email) {

    LocalDate startDate = LocalDate.parse(start);
    LocalDate endDate = LocalDate.parse(end);

    List<Expense> expenses = expenseService.getExpensesByDateRangeAndUser(startDate, endDate, email);
    return ResponseEntity.ok(expenses);
}


    // 📊 Category-wise summary
    @GetMapping("/summary/category")
    public ResponseEntity<Map<String, Double>> getExpenseSummaryByCategory(
            @RequestParam String email) {

        return ResponseEntity.ok(expenseService.getExpenseSummaryByCategory(email));
    }

    // 📅 Monthly summary
    @GetMapping("/summary/monthly")
    public ResponseEntity<Map<Integer, Double>> getMonthlyExpenseSummary(
            @RequestParam String email) {

        return ResponseEntity.ok(expenseService.getMonthlyExpenseSummary(email));
    }
}
