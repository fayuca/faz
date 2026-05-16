package com.example.faz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
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
	void shouldSave() {
		Transaction input = new Transaction();
		input.setAmount(new BigDecimal("50.00"));

		Transaction saved = new Transaction();
		saved.setId(1L);
		saved.setAmount(new BigDecimal("50.00"));

		when(repository.save(refEq(input))).thenReturn(saved);

		TransactionResponse response = service.create(input.toRequest());

		assertEquals(1L, response.getId());
		assertEquals(new BigDecimal("50.00"), response.getAmount());
	}

	@Test
	void shouldReturnAll() {
		when(repository.findAll())
				.thenReturn(List.of(
						transaction("1.00", null),
						transaction("2.00", null),
						transaction("3.00", null)));

		List<TransactionResponse> result = service.getAll(null);

		assertEquals(result.size(), 3);
		assertEquals(result.get(0).getAmount(), new BigDecimal("1.00"));
		assertEquals(result.get(1).getAmount(), new BigDecimal("2.00"));
		assertEquals(result.get(2).getAmount(), new BigDecimal("3.00"));
	}

	@Test
	void shouldReturnByDescription() {
		when(repository.findByDescriptionContainingIgnoreCase("b"))
				.thenReturn(List.of(transaction("2.00", "b")));

		List<TransactionResponse> result = service.getAll("b");

		assertEquals(result.size(), 1);
		assertEquals(result.get(0).getAmount(), new BigDecimal("2.00"));
		assertEquals(result.get(0).getDescription(), "b");
	}

	@Test
	void shouldUpdate() {
		Transaction existing = new Transaction();
		existing.setId(1L);
		existing.setAmount(new BigDecimal("100.00"));
		existing.setDescription("Old");

		TransactionRequest request = new TransactionRequest(
				new BigDecimal("200.00"),
				"Updated");

		when(repository.findById(1L)).thenReturn(Optional.of(existing));
		when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TransactionResponse result = service.update(1L, request);

		assertEquals("Updated", result.getDescription());
		assertEquals(0, result.getAmount().compareTo(new BigDecimal("200.00")));
	}

	@Test
	void shouldNotUpdateNotFound() {
		TransactionRequest request = new TransactionRequest(
				new BigDecimal("100.00"),
				"Test");

		when(repository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.update(1L, request));
	}

	private Transaction transaction(String amount, String description) {
		Transaction t = new Transaction();
		t.setAmount(new BigDecimal(amount));
		t.setDescription(description);
		return t;
	}
}
