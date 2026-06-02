package systems.redtape.faz.service;

import static systems.redtape.faz.support.MockMvcJsonSupport.assertTransactionResponse;
import static systems.redtape.faz.support.TransactionTestFixtures.TEST_DATE;
import static systems.redtape.faz.support.TransactionTestFixtures.randomCategory;
import static systems.redtape.faz.support.TransactionTestFixtures.request;
import static systems.redtape.faz.support.TransactionTestFixtures.transaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

import systems.redtape.faz.constants.ApiInfo;
import systems.redtape.faz.constants.FazDefaults;
import systems.redtape.faz.dto.Currency;
import systems.redtape.faz.dto.TransactionCategory;
import systems.redtape.faz.dto.TransactionCriteria;
import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionResponse;
import systems.redtape.faz.entity.Transaction;
import systems.redtape.faz.exception.ResourceNotFoundException;
import systems.redtape.faz.repository.TransactionRepository;

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
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";
		TransactionRequest request = request(amount, description);

		when(repository.save(refEq(Transaction.from(request))))
				.thenReturn(transaction(id, amount, description));

		TransactionResponse response = service.create(request);

		assertTransactionResponse(response, id, amount, description, TEST_DATE);
	}

	@SuppressWarnings("unchecked")
	@Test
	void shouldReturnByCriteria() {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";
		TransactionCriteria criteria = new TransactionCriteria();

		when(repository.findAll(any(Specification.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(transaction(id, amount, description))));

		Page<TransactionResponse> page = service.getAll(
				criteria,
				PageRequest.of(0, 10, Sort.by("amount").descending()));

		assertEquals(1, page.getSize());
		assertTransactionResponse(page.getContent().getFirst(), id, amount, description, TEST_DATE);
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
		TransactionRequest updateRequest = new TransactionRequest(newDate, newAmount, newDescription, newCategory);

		when(repository.findById(id)).thenReturn(Optional.of(existing));
		when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

		TransactionResponse response = service.update(id, updateRequest);

		assertTransactionResponse(response, id, newAmount, newDescription, newDate);
		assertEquals(newCategory, response.getCategory());
	}

	@Test
	void shouldPreserveCurrencyOnV1Update() {
		Long id = 1L;
		Transaction existing = transaction(
				id,
				new BigDecimal("50.00"),
				"EUR lunch",
				TransactionCategory.FOOD,
				Currency.EUR);
		TransactionRequest updateRequest = request(
				TEST_DATE,
				new BigDecimal("55.00"),
				"EUR lunch updated",
				TransactionCategory.TRANSPORT);

		when(repository.findById(id)).thenReturn(Optional.of(existing));
		when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

		service.update(id, updateRequest);

		assertEquals(Currency.EUR, existing.getCurrency());
	}

	@Test
	void shouldApplyBookCurrencyOnV1Create() {
		TransactionRequest request = request(new BigDecimal("10.00"), "Lunch");

		Transaction mapped = Transaction.from(request);

		assertEquals(FazDefaults.BOOK_CURRENCY, mapped.getCurrency());
	}

	@Test
	void shouldNotUpdateNotFound() {
		Long id = 1L;
		TransactionRequest updateRequest = request(new BigDecimal("100.00"), "Test");

		when(repository.findById(id)).thenReturn(Optional.empty());

		assertEquals(
				ApiInfo.notFound(id),
				assertThrows(ResourceNotFoundException.class, () -> service.update(id, updateRequest))
						.getMessage());
	}
}
