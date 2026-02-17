package com.example.expensetracker.serivce;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.ExpenseServiceImpl;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void shouldSaveExpenseSuccessfully() {
        Expense expense = new Expense();
        expense.setTitle("Food");
        expense.setAmount(200.0);

        User user = new User();
        user.setEmail("test@example.com");

        // Mock user repository to return user
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Mock expense repository to return expense
        when(expenseRepository.save(expense)).thenReturn(expense);

        Expense savedExpense = expenseService.saveExpense(expense, "test@example.com");

        assertEquals("Food", savedExpense.getTitle());
        assertEquals(200.0, savedExpense.getAmount());
    }

    @Test
    void shouldReturnExpenseWhenIdExists() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setTitle("Travel");

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        Expense result = expenseService.getExpenseById(1L);

        assertEquals("Travel", result.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenExpenseNotFound() {
        when(expenseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.getExpenseById(1L);
        });
    }

    @Test
    void shouldDeleteExpenseSuccessfully() {
        Expense expense = new Expense();
        expense.setId(1L);

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        expenseService.deleteExpense(1L);

        verify(expenseRepository).delete(expense);
    }
}
