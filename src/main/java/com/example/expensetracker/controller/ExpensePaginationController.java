package com.example.expensetracker.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses/page")
public class ExpensePaginationController {

    private final ExpenseService expenseService;

    public ExpensePaginationController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // 📄 Pagination only
    @GetMapping
    public ResponseEntity<Page<Expense>> getExpensesPaged(
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(expenseService.getAllExpenses(page, size));
    }

    // 🔃 Pagination + Sorting
    @GetMapping("/sorted")
    public ResponseEntity<Page<Expense>> getExpensesPagedAndSorted(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String direction) {

        return ResponseEntity.ok(
                expenseService.getExpensesPagedAndSorted(page, size, sortBy, direction)
        );
    }
}

