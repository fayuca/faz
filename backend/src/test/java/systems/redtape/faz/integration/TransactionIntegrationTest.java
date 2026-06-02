package systems.redtape.faz.integration;

import static systems.redtape.faz.support.ApiErrorAssertions.assertApiError;
import static systems.redtape.faz.support.MockMvcJsonSupport.assertTransactionEntity;
import static systems.redtape.faz.support.MockMvcJsonSupport.assertTransactionResponse;
import static systems.redtape.faz.support.MockMvcJsonSupport.apiError;
import static systems.redtape.faz.support.MockMvcJsonSupport.doGet;
import static systems.redtape.faz.support.MockMvcJsonSupport.doPost;
import static systems.redtape.faz.support.MockMvcJsonSupport.doPut;
import static systems.redtape.faz.support.MockMvcJsonSupport.transactionPageContent;
import static systems.redtape.faz.support.MockMvcJsonSupport.transactionResponse;
import static systems.redtape.faz.support.TransactionTestFixtures.TEST_DATE;
import static systems.redtape.faz.support.TransactionTestFixtures.amount;
import static systems.redtape.faz.support.TransactionTestFixtures.description;
import static systems.redtape.faz.support.TransactionTestFixtures.randomCategory;
import static systems.redtape.faz.support.TransactionTestFixtures.request;
import static systems.redtape.faz.support.TransactionTestFixtures.transaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import systems.redtape.faz.constants.ApiInfo;
import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionResponse;
import systems.redtape.faz.entity.Transaction;
import systems.redtape.faz.repository.TransactionRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionIntegrationTest {
	private static final String TRANSACTIONS = ApiPaths.V1_TRANSACTIONS;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransactionRepository repository;

	// -- TESTS

	@Test
	void shouldFindPaged() throws Exception {
		int total = 22;
		int page = 2;
		int size = 10;
		int first = (page * size) + 1;

		seedTransactions(total);

		// *

		List<TransactionResponse> responses = transactionPageContent(
				objectMapper,
				doGet(mockMvc, TRANSACTIONS + "?page=" + page + "&size=" + size + "&sort=amount,asc")
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());

		// 1

		assertEquals(Math.min(total - (page * size), size), responses.size());
		assertTransactionResponse(responses.getFirst(), amount(first), description(first), TEST_DATE);
	}

	@Test
	void shouldFindPagedByCriteria() throws Exception {
		int total = 27;
		int page = 0;
		int size = 10;
		int pivot = 3;
		String found = "Found";
		int first = (page * size) + pivot;

		seedTransactions(total, i -> transaction(amount(i), i % pivot == 0 ? found : "NotMe"));

		// *

		List<TransactionResponse> responses = transactionPageContent(
				objectMapper,
				doGet(
						mockMvc,
						TRANSACTIONS + "?description=" + found + "&page=" + page + "&size=" + size + "&sort=amount,asc")
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());

		// 1

		assertEquals(9, responses.size());
		assertTransactionResponse(responses.getFirst(), amount(first), found, TEST_DATE);
	}

	@Test
	void shouldCreate() throws Exception {
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";
		TransactionRequest createRequest = request(amount, description);

		// *

		TransactionResponse response = transactionResponse(
				objectMapper,
				doPost(mockMvc, objectMapper, TRANSACTIONS, createRequest)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());
		Long id = response.getId();

		// 1

		assertTransactionResponse(response, amount, description, TEST_DATE);

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransactionEntity(queried, id, amount, description, TEST_DATE);

		// 3

		TransactionResponse requested = transactionResponse(
				objectMapper,
				doGet(mockMvc, TRANSACTIONS + "/" + id)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());
		assertTransactionResponse(requested, amount, description, TEST_DATE);
	}

	@Test
	void shouldUpdate() throws Exception {
		BigDecimal oldAmount = new BigDecimal("100.00");
		String oldDescription = "Old";
		Long id = repository.save(transaction(oldAmount, oldDescription)).getId();

		BigDecimal newAmount = new BigDecimal("200.00");
		String newDescription = "New";
		LocalDateTime newDate = LocalDateTime.of(2026, 6, 1, 9, 30);
		TransactionRequest updateRequest = new TransactionRequest(newDate, newAmount, newDescription, randomCategory());

		// *

		TransactionResponse response = transactionResponse(
				objectMapper,
				doPut(mockMvc, objectMapper, TRANSACTIONS + "/" + id, updateRequest)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());

		// 1

		assertTransactionResponse(response, newAmount, newDescription, newDate);

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransactionEntity(queried, id, newAmount, newDescription, newDate);

		// 3

		TransactionResponse requested = transactionResponse(
				objectMapper,
				doGet(mockMvc, TRANSACTIONS + "/" + id)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());
		assertTransactionResponse(requested, newAmount, newDescription, newDate);
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		Long id = -1L;

		// *

		assertApiError(
				apiError(
						objectMapper,
						doPut(mockMvc, objectMapper, TRANSACTIONS + "/" + id, request(new BigDecimal("200.00"), "New"))
								.andDo(print())
								.andExpect(status().isNotFound())
								.andReturn()),
				ApiInfo.notFound(id));
	}

	@Test
	void shouldNotCreateBlankDescription() throws Exception {
		// *

		assertApiError(
				apiError(
						objectMapper,
						doPost(mockMvc, objectMapper, TRANSACTIONS, request(new BigDecimal("100.00"), ""))
								.andDo(print())
								.andExpect(status().isBadRequest())
								.andReturn()),
				ApiInfo.ERR_VALIDATION_FAILED,
				"description");
	}

	@Test
	void shouldNotCreateWhitespaceDescription() throws Exception {
		// *

		assertApiError(
				apiError(
						objectMapper,
						doPost(mockMvc, objectMapper, TRANSACTIONS, request(new BigDecimal("100.00"), "   "))
								.andDo(print())
								.andExpect(status().isBadRequest())
								.andReturn()),
				ApiInfo.ERR_VALIDATION_FAILED,
				"description");
	}

	@Test
	void shouldNotUpdateBlankDescription() throws Exception {
		BigDecimal oldAmount = new BigDecimal("100.00");
		String oldDescription = "Old";
		Long id = repository.save(transaction(oldAmount, oldDescription)).getId();

		// *

		assertApiError(
				apiError(
						objectMapper,
						doPut(mockMvc, objectMapper, TRANSACTIONS + "/" + id, request(new BigDecimal("200.00"), ""))
								.andDo(print())
								.andExpect(status().isBadRequest())
								.andReturn()),
				ApiInfo.ERR_VALIDATION_FAILED,
				"description");

		// 1

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransactionEntity(queried, id, oldAmount, oldDescription, TEST_DATE);
	}

	@Test
	void shouldNotFindWithNegativeMinAmount() throws Exception {
		// *

		assertApiError(
				apiError(
						objectMapper,
						doGet(mockMvc, TRANSACTIONS + "?minAmount=-1")
								.andDo(print())
								.andExpect(status().isBadRequest())
								.andReturn()),
				ApiInfo.ERR_VALIDATION_FAILED,
				"minAmount");
	}

	@Test
	void shouldNotFindWithInvalidAmountRange() throws Exception {
		// *

		assertApiError(
				apiError(
						objectMapper,
						doGet(mockMvc, TRANSACTIONS + "?minAmount=50&maxAmount=10")
								.andDo(print())
								.andExpect(status().isBadRequest())
								.andReturn()),
				ApiInfo.ERR_VALIDATION_FAILED,
				"amountRangeValid");
	}

	@Test
	void shouldNotFindWithInvalidDateRange() throws Exception {
		// *

		assertApiError(
				apiError(
						objectMapper,
						doGet(mockMvc, TRANSACTIONS + "?from=2026-06-01&to=2026-05-01")
								.andDo(print())
								.andExpect(status().isBadRequest())
								.andReturn()),
				ApiInfo.ERR_VALIDATION_FAILED,
				"dateRangeValid");
	}

	@Test
	void shouldNotUpdateInvalid() throws Exception {
		BigDecimal oldAmount = new BigDecimal("100.00");
		String oldDescription = "Old";
		Long id = repository.save(transaction(oldAmount, oldDescription)).getId();

		TransactionRequest updateRequest = new TransactionRequest(
				null,
				new BigDecimal("-10.00"),
				null,
				randomCategory());

		// *

		assertApiError(
				apiError(
						objectMapper,
						doPut(mockMvc, objectMapper, TRANSACTIONS + "/" + id, updateRequest)
								.andDo(print())
								.andExpect(status().isBadRequest())
								.andReturn()),
				ApiInfo.ERR_VALIDATION_FAILED,
				"date",
				"amount",
				"description");

		// 1

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransactionEntity(queried, id, oldAmount, oldDescription, TEST_DATE);

		// 2

		TransactionResponse requested = transactionResponse(
				objectMapper,
				doGet(mockMvc, TRANSACTIONS + "/" + id)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());
		assertTransactionResponse(requested, oldAmount, oldDescription, TEST_DATE);
	}

	// -- FACTORIES

	private List<Transaction> seedTransactions(int count) {
		return seedTransactions(count, i -> transaction(amount(i), description(i), randomCategory()));
	}

	private List<Transaction> seedTransactions(int count, Function<Integer, Transaction> factory) {
		List<Transaction> transactions = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			transactions.add(factory.apply(i));
		}

		return repository.saveAll(transactions);
	}
}
