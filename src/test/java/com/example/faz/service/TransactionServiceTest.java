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
import com.example.faz.exception.ApiErrors;
import com.example.faz.exception.ResourceNotFoundException;
import com.example.faz.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	@Mock
	private TransactionRepository repository;

	@InjectMocks
	private TransactionService service;

	// -- TESTS

	@Test
	void shouldSave() {
		Long id = 1L;
		String amount = "100.00";
		String description = "Test";

		TransactionRequest request = request(amount, description);

		when(repository
				.save(refEq(Transaction.from(request))))
				.thenReturn(transaction(id, amount, description));

		TransactionResponse response = service.create(request);

		assertResponse(response, id, amount, description);
	}

	@Test
	void shouldReturnAll() {
		Long id = 1L;
		String amount = "100.00";
		String description = "Test";

		when(repository
				.findAll())
				.thenReturn(List.of(transaction(id, amount, description)));

		List<TransactionResponse> responses = service.getAll(null);

		assertEquals(1, responses.size());
		assertResponse(responses.getFirst(), id, amount, description);
	}

	@Test
	void shouldReturnByDescription() {
		Long id = 1L;
		String amount = "100.00";
		String description = "Test";

		when(repository
				.findByDescriptionContainingIgnoreCase(description))
				.thenReturn(List.of(transaction(id, amount, description)));

		List<TransactionResponse> responses = service.getAll(description);

		assertEquals(1, responses.size());
		assertResponse(responses.getFirst(), id, amount, description);
	}

	@Test
	void shouldUpdate() {
		Long id = 1L;

		String oldAmount = "100.00";
		String oldDescription = "Old";

		String newAmount = "200.00";
		String newDescription = "New";

		Transaction existing = transaction(id, oldAmount, oldDescription);
		TransactionRequest request = request(newAmount, newDescription);

		when(repository.findById(id)).thenReturn(Optional.of(existing));
		when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TransactionResponse response = service.update(id, request);

		assertResponse(response, id, newAmount, newDescription);
	}

	@Test
	void shouldNotUpdateNotFound() {
		Long id = 1L;
		String amount = "100.00";
		String description = "Test";

		TransactionRequest request = request(amount, description);

		when(repository.findById(id)).thenReturn(Optional.empty());

		assertEquals(
				ApiErrors.notFound(id),
				assertThrows(ResourceNotFoundException.class, () -> service.update(id, request)).getMessage());
		;
	}

	// -- ASSERT

	private void assertResponse(TransactionResponse response, Long id, String amount, String description) {
		assertEquals(id, response.getId());
		assertEquals(amount, response.getAmount().toString());
		assertEquals(description, response.getDescription());
	}

	// -- FACTORIES

	private TransactionRequest request(String amount, String description) {
		return new TransactionRequest(new BigDecimal(amount), description);
	}

	private Transaction transaction(Long id, String amount, String description) {
		Transaction transaction = new Transaction();
		transaction.setId(id);
		transaction.setAmount(new BigDecimal(amount));
		transaction.setDescription(description);
		return transaction;
	}
}
