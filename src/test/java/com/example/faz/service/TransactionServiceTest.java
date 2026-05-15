package com.example.faz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.faz.dto.TransactionRequestDTO;
import com.example.faz.dto.TransactionResponseDTO;
import com.example.faz.entity.Transaction;
import com.example.faz.exception.ResourceNotFoundException;
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

		when(repository.save(refEq(input))).thenReturn(saved);

		TransactionResponseDTO response = service.create(input.toRequestDTO());

		assertEquals(1L, response.getId());
		assertEquals(new BigDecimal("50.00"), response.getAmount());
	}

	@Test
	void shouldReturnAllTransactions() {
		when(repository.findAll()).thenReturn(List.of());

		List<TransactionResponseDTO> result = service.getAll();

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldUpdateTransaction() {
		Transaction existing = new Transaction();
		existing.setId(1L);
		existing.setAmount(new BigDecimal("100.00"));
		existing.setDescription("Old");

		TransactionRequestDTO request = new TransactionRequestDTO(
				new BigDecimal("200.00"),
				"Updated");

		when(repository.findById(1L)).thenReturn(Optional.of(existing));
		when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TransactionResponseDTO result = service.update(1L, request);

		assertEquals("Updated", result.getDescription());
		assertEquals(0, result.getAmount().compareTo(new BigDecimal("200.00")));
	}

	@Test
	void shouldThrowWhenUpdatingMissingTransaction() {
		TransactionRequestDTO request = new TransactionRequestDTO(
				new BigDecimal("100.00"),
				"Test");

		when(repository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.update(1L, request));
	}
}
