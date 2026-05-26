package com.example.faz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.example.faz.constants.ApiInfo;
import com.example.faz.dto.TransactionCategory;
import com.example.faz.dto.TransactionCriteria;
import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.entity.Transaction;
import com.example.faz.exception.ResourceNotFoundException;
import com.example.faz.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	private static final LocalDateTime TEST_DATE = LocalDateTime.of(2026, 5, 26, 12, 0);

	@Mock
	private TransactionRepository repository;

	@InjectMocks
	private TransactionService service;

	@Test
	void shouldSave() {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		TransactionRequest request = request(amount, description);

		when(repository
				.save(refEq(Transaction.from(request))))
				.thenReturn(transaction(id, amount, description));

		TransactionResponse response = service.create(request);

		assertResponse(response, id, amount, description, TEST_DATE);
	}

	@SuppressWarnings("unchecked")
	@Test
	void shouldReturnByCriteria() {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		TransactionCriteria criteria = new TransactionCriteria();

		when(repository.findAll(any(Specification.class), any(Pageable.class)))
				.thenReturn(new PageImpl<Transaction>(List.of(transaction(id, amount, description))));

		Page<TransactionResponse> page = service.getAll(criteria,
				PageRequest.of(0, 10, Sort.by("amount").descending()));

		assertEquals(1, page.getSize());
		assertResponse(page.getContent().getFirst(), id, amount, description, TEST_DATE);
	}

	@Test
	void shouldUpdate() {
		Long id = 1L;

		BigDecimal oldAmount = new BigDecimal("100.00");
		String oldDescription = "Old";

		BigDecimal newAmount = new BigDecimal("200.00");
		String newDescription = "New";
		TransactionCategory newCategory = TransactionCategory.TRANSPORT;
		LocalDateTime newDate = LocalDateTime.of(2026, 6, 1, 9, 30);

		Transaction existing = transaction(id, oldAmount, oldDescription);
		existing.setCategory(TransactionCategory.FOOD);
		TransactionRequest request = new TransactionRequest(newDate, newAmount, newDescription, newCategory);

		when(repository.findById(id)).thenReturn(Optional.of(existing));
		when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TransactionResponse response = service.update(id, request);

		assertResponse(response, id, newAmount, newDescription, newDate);
		assertEquals(newCategory, response.getCategory());
	}

	@Test
	void shouldNotUpdateNotFound() {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		TransactionRequest request = request(amount, description);

		when(repository.findById(id)).thenReturn(Optional.empty());

		assertEquals(
				ApiInfo.notFound(id),
				assertThrows(ResourceNotFoundException.class, () -> service.update(id, request)).getMessage());
	}

	private void assertResponse(TransactionResponse response, Long id, BigDecimal amount, String description,
			LocalDateTime date) {
		assertEquals(id, response.getId());
		assertEquals(amount, response.getAmount());
		assertEquals(description, response.getDescription());
		assertEquals(date, response.getDate());
	}

	private TransactionRequest request(BigDecimal amount, String description) {
		return new TransactionRequest(TEST_DATE, amount, description, randomCategory());
	}

	private Transaction transaction(Long id, BigDecimal amount, String description) {
		Transaction transaction = new Transaction();
		transaction.setId(id);
		transaction.setDate(TEST_DATE);
		transaction.setAmount(amount);
		transaction.setDescription(description);
		return transaction;
	}

	private TransactionCategory randomCategory() {
		TransactionCategory[] categories = TransactionCategory.values();
		return categories[ThreadLocalRandom.current().nextInt(categories.length)];
	}
}
