package com.example.expensetracker.mapper;

import com.example.expensetracker.dto.ExpenseDTO;
import com.example.expensetracker.entity.Expense;

public class ExpenseMapper {

    public static Expense toEntity(ExpenseDTO dto) {
        Expense expense = new Expense();
        expense.setId(dto.getId());
        expense.setTitle(dto.getTitle());
        expense.setCategory(dto.getCategory());
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        return expense;
    }

    public static ExpenseDTO toDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setCategory(expense.getCategory());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        return dto;
    }
}
