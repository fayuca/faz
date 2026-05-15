package com.example.faz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.faz.entity.Transaction;
import com.example.faz.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

	@Mock
	private TransactionRepository repository;

	@InjectMocks
	private TransactionService service;

	@Test
	void shouldSaveTransaction() {
		Transaction input = new Transaction();
		input.setAmount(new BigDecimal("50.00"));

		Transaction saved = new Transaction();
		saved.setId(1L);
		saved.setAmount(new BigDecimal("50.00"));

		when(repository.save(input)).thenReturn(saved);

		Transaction result = service.create(input);

		assertEquals(1L, result.getId());
		assertEquals(new BigDecimal("50.00"), result.getAmount());
	}

	@Test
	void shouldReturnAllTransactions() {
		when(repository.findAll()).thenReturn(List.of());

		List<Transaction> result = service.getAll();

		assertTrue(result.isEmpty());
	}
}
