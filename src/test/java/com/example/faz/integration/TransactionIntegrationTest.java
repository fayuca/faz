package com.example.faz.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.example.faz.constants.ApiInfo;
import com.example.faz.dto.TransactionCategory;
import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.entity.Transaction;
import com.example.faz.exception.ApiError;
import com.example.faz.repository.TransactionRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionIntegrationTest {
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

		List<TransactionResponse> responses = responses(
				doGet("/transactions"
						+ "?page=" + page
						+ "&size=" + size
						+ "&sort=" + "amount,asc")
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());

		// 1

		assertEquals(Math.min(total - (page * size), size), responses.size());
		assertResponse(responses.getFirst(), amount(first), description(first));
	}

	@Test
	void shouldFindPagedByCriteria() throws Exception {
		int total = 27;
		int page = 0;
		int size = 10;
		String sort = "amount,asc";

		int pivot = 3;

		String found = "Found";
		int first = (page * size) + pivot;

		seedTransactions(total,
				i -> transaction(
						amount(i),
						i % pivot == 0 ? found : "NotMe"));

		// *

		List<TransactionResponse> responses = responses(
				doGet("/transactions"
						+ "?description=" + found
						+ "&page=" + page
						+ "&size=" + size
						+ "&sort=" + sort)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());

		// 1

		assertEquals(9, responses.size());
		assertResponse(responses.getFirst(), amount(first), found);
	}

	@Test
	void shouldCreate() throws Exception {
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		TransactionRequest request = request(amount, description);

		// *

		TransactionResponse response = response(
				doPost("/transactions", request)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());
		Long id = response.getId();

		// 1

		assertResponse(response, amount, description);

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransaction(queried, id, amount, description);

		// 3

		TransactionResponse requested = response(doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn());
		assertResponse(requested, amount, description);
	}

	@Test
	void shouldUpdate() throws Exception {
		BigDecimal oldAmount = new BigDecimal("100.00");
		String oldDescription = "Old";
		Long id = repository.save(transaction(oldAmount, oldDescription)).getId();

		BigDecimal newAmount = new BigDecimal("200.00");
		String newDescription = "New";
		TransactionRequest request = request(newAmount, newDescription);

		// *

		TransactionResponse response = response(
				doPut("/transactions/" + id, request)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());

		// 1

		assertResponse(response, newAmount, newDescription);

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransaction(queried, id, newAmount, newDescription);

		// 3

		TransactionResponse requested = response(doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn());
		assertResponse(requested, newAmount, newDescription);
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		Long id = -1L;

		// *

		ApiError apiError = apiError(
				doPut("/transactions/" + id, request(new BigDecimal("200.00"), "New"))
						.andDo(print())
						.andExpect(status().isNotFound())
						.andReturn());

		// 1

		assertApiError(apiError, ApiInfo.notFound(id));
	}

	@Test
	void shouldNotUpdateInvalid() throws Exception {
		BigDecimal oldAmount = new BigDecimal("100.00");
		String oldDescription = "Old";
		Long id = repository.save(transaction(oldAmount, oldDescription)).getId();

		BigDecimal newAmount = new BigDecimal("-10.00");
		String newDescription = null;
		TransactionRequest request = request(newAmount, newDescription);

		// *

		ApiError apiError = apiError(
				doPut("/transactions/" + id, request)
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andReturn());

		// 1

		assertApiError(apiError, ApiInfo.ERR_VALIDATION_FAILED, "amount", "description");

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransaction(queried, id, oldAmount, oldDescription);

		// 3

		TransactionResponse requested = response(doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn());
		assertResponse(requested, oldAmount, oldDescription);
	}

	// -- ASSERT

	private void assertApiError(ApiError apiError, String message, String... fields) {
		assertEquals(message, apiError.getMessage());
		for (String field : fields) {
			assertTrue(apiError.getFieldErrors().containsKey(field));
		}
	}

	private void assertResponse(TransactionResponse response, BigDecimal amount, String description) {
		assertEquals(0, amount.compareTo(response.getAmount()));
		assertEquals(description, response.getDescription());
	}

	private void assertTransaction(Transaction transaction, Long id, BigDecimal amount, String description) {
		assertEquals(id, transaction.getId());
		assertEquals(0, amount.compareTo(transaction.getAmount()));
		assertEquals(description, transaction.getDescription());
	}

	// -- FACTORIES

	private BigDecimal amount(int i) {
		return new BigDecimal(i * 100);
	}

	private String description(int i) {
		return "Transaction " + i;
	}

	private TransactionRequest request(BigDecimal amount, String description) {
		return new TransactionRequest(amount, description, randomCategory());
	}

	private Transaction transaction(BigDecimal amount, String description) {
		return transaction(amount, description, randomCategory());
	}

	private Transaction transaction(BigDecimal amount, String description, TransactionCategory category) {
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setDescription(description);
		transaction.setCategory(category);
		return transaction;
	}

	private List<Transaction> seedTransactions(int count) {
		return seedTransactions(count, i -> transaction(amount(i), description(i), randomCategory()));
	}

	private TransactionCategory randomCategory() {
		TransactionCategory[] categories = TransactionCategory.values();
		return categories[ThreadLocalRandom.current().nextInt(categories.length)];
	}

	private List<Transaction> seedTransactions(int count, Function<Integer, Transaction> factory) {
		List<Transaction> transactions = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			transactions.add(factory.apply(i));
		}

		return repository.saveAll(transactions);
	}

	// -- MAPPERS

	private ApiError apiError(MvcResult result) throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
	}

	private TransactionResponse response(MvcResult result) throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);
	}

	private List<TransactionResponse> responses(MvcResult result)
			throws Exception {
		JsonNode root = objectMapper.readTree(
				result.getResponse().getContentAsString());

		return objectMapper.readValue(
				root.get("content").toString(),
				new TypeReference<List<TransactionResponse>>() {
				});
	}

	// -- METHODS

	private ResultActions doGet(String url) throws Exception {
		return mockMvc.perform(get(url));
	}

	private ResultActions doPost(String url, TransactionRequest request) throws Exception {
		return mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)));
	}

	private ResultActions doPut(String url, TransactionRequest request) throws Exception {
		return mockMvc.perform(put(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)));
	}
}
