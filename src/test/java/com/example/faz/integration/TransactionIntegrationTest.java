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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.entity.Transaction;
import com.example.faz.exception.ApiError;
import com.example.faz.exception.ApiErrors;
import com.example.faz.repository.TransactionRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionIntegrationTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransactionRepository repository;

	// -- TESTS

	@Test
	void shouldCreate() throws Exception {
		String amount = "100.00";
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

		assertResponse(response, id, amount, description);

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransaction(queried, id, amount, description);

		// 3

		TransactionResponse requested = response(doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn());
		assertResponse(requested, id, amount, description);
	}

	@Test
	void shouldUpdate() throws Exception {
		String oldAmount = "100.00";
		String oldDescription = "Old";
		Long id = repository.save(transaction(oldAmount, oldDescription)).getId();

		String newAmount = "200.00";
		String newDescription = "New";
		TransactionRequest request = request(newAmount, newDescription);

		// *

		TransactionResponse response = response(
				doPut("/transactions/" + id, request)
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn());

		// 1

		assertResponse(response, id, newAmount, newDescription);

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransaction(queried, id, newAmount, newDescription);

		// 3

		TransactionResponse requested = response(doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn());
		assertResponse(requested, id, newAmount, newDescription);
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		Long id = -1L;

		// *

		ApiError apiError = apiError(
				doPut("/transactions/" + id, request("200.00", "New"))
						.andDo(print())
						.andExpect(status().isNotFound())
						.andReturn());

		// 1

		assertApiError(apiError, ApiErrors.notFound(id));
	}

	@Test
	void shouldNotUpdateInvalid() throws Exception {
		String oldAmount = "100.00";
		String oldDescription = "Old";
		Long id = repository.save(transaction(oldAmount, oldDescription)).getId();

		String newAmount = "-10.00";
		String newDescription = null;
		TransactionRequest request = request(newAmount, newDescription);

		// *

		ApiError apiError = apiError(
				doPut("/transactions/" + id, request)
						.andDo(print())
						.andExpect(status().isBadRequest())
						.andReturn());

		// 1

		assertApiError(apiError, ApiErrors.VALIDATION_FAILED, "amount", "description");

		// 2

		Transaction queried = repository.findById(id).orElseThrow();
		assertTransaction(queried, id, oldAmount, oldDescription);

		// 3

		TransactionResponse requested = response(doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn());
		assertResponse(requested, id, oldAmount, oldDescription);
	}

	// -- ASSERT

	private void assertApiError(ApiError apiError, String message, String... fields) {
		assertEquals(message, apiError.getMessage());
		for (String field : fields) {
			assertTrue(apiError.getFieldErrors().containsKey(field));
		}
	}

	private void assertResponse(TransactionResponse response, Long id, String amount, String description) {
		assertEquals(id, response.getId());
		assertEquals(amount, response.getAmount().toString());
		assertEquals(description, response.getDescription());
	}

	private void assertTransaction(Transaction transaction, Long id, String amount, String description) {
		assertEquals(id, transaction.getId());
		assertEquals(amount, transaction.getAmount().toString());
		assertEquals(description, transaction.getDescription());
	}

	// -- FACTORIES

	private TransactionRequest request(String amount, String description) {
		return new TransactionRequest(new BigDecimal(amount), description);
	}

	private Transaction transaction(String amount, String description) {
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal(amount));
		transaction.setDescription(description);
		return transaction;
	}

	// -- MAPPERS

	private ApiError apiError(MvcResult result) throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
	}

	private TransactionResponse response(MvcResult result) throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);
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
