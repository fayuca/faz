package com.example.faz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.example.faz.constants.ApiInfo;
import com.example.faz.dto.TransactionCategory;
import com.example.faz.dto.TransactionCriteria;
import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.exception.ApiError;
import com.example.faz.exception.ResourceNotFoundException;
import com.example.faz.service.TransactionService;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TransactionService service;

	private static final String TRANSACTIONS = "/api/transactions";

	// -- TESTS

	@Test
	void shouldCreate() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		when(service
				.create(any(TransactionRequest.class)))
				.thenReturn(response(id, amount, description));

		MvcResult result = doPost(TRANSACTIONS, request(amount, description))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		TransactionResponse response = response(result);

		assertResponse(response, id, amount, description);
	}

	@Test
	void shouldNotCreateInvalid() throws Exception {
		BigDecimal amount = new BigDecimal("-10.00");
		String description = null;

		MvcResult result = doPost(TRANSACTIONS, request(amount, description))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();

		ApiError apiError = apiError(result);

		assertApiError(apiError, ApiInfo.ERR_VALIDATION_FAILED, "amount", "description");
	}

	@Test
	void shouldFind() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		when(service
				.get(1L))
				.thenReturn(response(id, amount, description));

		MvcResult result = doGet(TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		TransactionResponse response = response(result);

		assertResponse(response, id, amount, description);
	}

	@Test
	void shouldFindByCriteria() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		when(service
				.getAll(any(TransactionCriteria.class), any(Pageable.class)))
				.thenReturn(new PageImpl<TransactionResponse>(List.of(response(id, amount, description))));

		MvcResult result = doGet(TRANSACTIONS + "?description=" + description)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<TransactionResponse> responses = responses(result);
		assertEquals(1, responses.size());
		assertResponse(responses.getFirst(), id, amount, description);
	}

	@Test
	void shouldFindAllPaged() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		int page = 0;
		int size = 10;

		when(service
				.getAll(any(TransactionCriteria.class), any(Pageable.class)))
				.thenReturn(new PageImpl<TransactionResponse>(List.of(response(id, amount, description))));

		MvcResult result = doGet(TRANSACTIONS
				+ "?page=" + page
				+ "&size=" + size)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<TransactionResponse> responses = responses(result);
		assertEquals(1, responses.size());
		assertResponse(responses.getFirst(), id, amount, description);
	}

	@Test
	void shouldNotFind() throws Exception {
		Long id = -1L;
		String message = ApiInfo.notFound(id);

		when(service
				.get(id))
				.thenThrow(new ResourceNotFoundException(message));

		MvcResult result = doGet(TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		ApiError apiError = apiError(result);
		assertApiError(apiError, message);
	}

	@Test
	void shouldDelete() throws Exception {
		Long id = 1L;

		doNothing().when(service).delete(id);

		doDelete(TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isNoContent());

		verify(service).delete(id);
	}

	@Test
	void shouldNotDeleteNotFound() throws Exception {
		Long id = -1L;
		String message = ApiInfo.notFound(id);

		doThrow(new ResourceNotFoundException(message))
				.when(service)
				.delete(id);

		MvcResult result = doDelete(TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		ApiError apiError = apiError(result);
		assertApiError(apiError, message);
	}

	@Test
	void shouldUpdate() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("200.00");
		String description = "Updated";

		when(service
				.update(eq(id), any(TransactionRequest.class)))
				.thenReturn(response(id, amount, description));

		MvcResult result = doPut(TRANSACTIONS + "/" + id, request(amount, description))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		TransactionResponse response = response(result);
		assertResponse(response, id, amount, description);
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		Long id = -1L;
		BigDecimal amount = new BigDecimal("200.00");
		String description = "Updated";

		String message = ApiInfo.notFound(id);

		when(service
				.update(eq(id), any(TransactionRequest.class)))
				.thenThrow(new ResourceNotFoundException(message));

		MvcResult result = doPut(TRANSACTIONS + "/" + id, request(amount, description))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		ApiError apiError = apiError(result);
		assertApiError(apiError, message);
	}

	@Test
	void shouldNotUpdateInvalid() throws Exception {
		MvcResult result = doPut(TRANSACTIONS + "/" + "-1", request(new BigDecimal("-10.00"), null))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();

		ApiError apiError = apiError(result);
		assertApiError(apiError, ApiInfo.ERR_VALIDATION_FAILED, "amount", "description");
	}

	// -- ASSERT

	private void assertApiError(ApiError apiError, String message, String... fields) {
		assertEquals(message, apiError.getMessage());
		for (String field : fields) {
			assertTrue(apiError.getFieldErrors().containsKey(field));
		}
	}

	private void assertResponse(TransactionResponse response, Long id, BigDecimal amount, String description) {
		assertEquals(id, response.getId());
		assertEquals(0, amount.compareTo(response.getAmount()));
		assertEquals(description, response.getDescription());
	}

	// -- FACTORIES

	private TransactionRequest request(BigDecimal amount, String description) {
		return new TransactionRequest(amount, description, randomCategory());
	}

	private TransactionResponse response(Long id, BigDecimal amount, String description) {
		return new TransactionResponse(id, amount, description, randomCategory());
	}

	private TransactionCategory randomCategory() {
		TransactionCategory[] categories = TransactionCategory.values();
		return categories[ThreadLocalRandom.current().nextInt(categories.length)];
	}

	// -- MAPPERS

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

	private ApiError apiError(MvcResult result) throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
	}

	// -- METHODS

	private ResultActions doDelete(String url) throws Exception {
		return mockMvc.perform(delete(url));
	}

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
