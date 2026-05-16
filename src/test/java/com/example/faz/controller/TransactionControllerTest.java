package com.example.faz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.exception.ApiError;
import com.example.faz.exception.ApiErrors;
import com.example.faz.exception.ResourceNotFoundException;
import com.example.faz.service.TransactionService;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TransactionService service;

	// -- TESTS

	@Test
	void shouldCreate() throws Exception {
		Long id = 1L;
		String amount = "100.00";
		String description = "Test";

		when(service
				.create(any(TransactionRequest.class)))
				.thenReturn(response(id, amount, description));

		MvcResult result = doPost("/transactions", request(amount, description))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		TransactionResponse response = response(result);
		assertEquals(id, response.getId());
		assertEquals(amount, response.getAmount().toString());
		assertEquals(description, response.getDescription());
	}

	@Test
	void shouldNotCreateInvalid() throws Exception {
		String amount = "-10.00";
		String description = null;

		MvcResult result = doPost("/transactions", request(amount, description))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();

		ApiError apiError = apiError(result);
		assertEquals(ApiErrors.VALIDATION_FAILED, apiError.getMessage());
		assertTrue(apiError.getFieldErrors().containsKey("amount"));
		assertTrue(apiError.getFieldErrors().containsKey("description"));
	}

	@Test
	void shouldFind() throws Exception {
		Long id = 1L;
		String amount = "100.00";
		String description = "Test";

		when(service
				.get(1L))
				.thenReturn(response(id, amount, description));

		MvcResult result = doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		TransactionResponse response = response(result);
		assertEquals(id, response.getId());
		assertEquals(amount, response.getAmount().toString());
		assertEquals(description, response.getDescription());
	}

	@Test
	void shouldFindAll() throws Exception {
		Long id = 1L;
		String amount = "100.00";
		String description = null;

		when(service
				.getAll(description))
				.thenReturn(List.of(response(id, amount, description)));

		MvcResult result = doGet("/transactions")
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<TransactionResponse> responses = responses(result);
		assertEquals(1, responses.size());
		assertEquals(id, responses.get(0).getId());
		assertEquals(amount, responses.get(0).getAmount().toString());
		assertEquals(description, responses.get(0).getDescription());
	}

	@Test
	void shouldFindByDescription() throws Exception {
		Long id = 1L;
		String amount = "100.00";
		String description = "Test";

		when(service
				.getAll(description))
				.thenReturn(List.of(response(id, amount, description)));

		MvcResult result = doGet("/transactions?description=" + description)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<TransactionResponse> responses = responses(result);
		assertEquals(1, responses.size());
		assertEquals(id, responses.get(0).getId());
		assertEquals(amount, responses.get(0).getAmount().toString());
		assertEquals(description, responses.get(0).getDescription());
	}

	@Test
	void shouldNotFind() throws Exception {
		Long id = -1L;
		String message = ApiErrors.notFound(id);

		when(service
				.get(id))
				.thenThrow(new ResourceNotFoundException(message));

		MvcResult result = doGet("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		ApiError apiError = apiError(result);
		assertEquals(message, apiError.getMessage());
	}

	@Test
	void shouldDelete() throws Exception {
		Long id = 1L;

		doNothing().when(service).delete(id);

		doDelete("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void shouldNotDeleteNotFound() throws Exception {
		Long id = -1L;
		String message = ApiErrors.notFound(id);

		doThrow(new ResourceNotFoundException(message))
				.when(service)
				.delete(id);

		MvcResult result = doDelete("/transactions/" + id)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		ApiError apiError = apiError(result);
		assertEquals(message, apiError.getMessage());
	}

	@Test
	void shouldUpdate() throws Exception {
		Long id = 1L;
		String amount = "200.00";
		String description = "Updated";

		when(service
				.update(eq(id), any(TransactionRequest.class)))
				.thenReturn(response(id, amount, description));

		MvcResult result = doPut("/transactions/" + id, request(amount, description))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		TransactionResponse response = response(result);
		assertEquals(id, response.getId());
		assertEquals(amount, response.getAmount().toString());
		assertEquals(description, response.getDescription());
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		Long id = -1L;
		String amount = "200.00";
		String description = "Updated";

		String message = ApiErrors.notFound(id);

		when(service
				.update(eq(id), any(TransactionRequest.class)))
				.thenThrow(new ResourceNotFoundException(message));

		MvcResult result = doPut("/transactions/" + id, request(amount, description))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		ApiError apiError = apiError(result);
		assertEquals(message, apiError.getMessage());
	}

	@Test
	void shouldNotUpdateInvalid() throws Exception {
		MvcResult result = doPut("/transactions/-1", request("-10.00", null))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();

		ApiError apiError = apiError(result);
		assertEquals(ApiErrors.VALIDATION_FAILED, apiError.getMessage());
		assertTrue(apiError.getFieldErrors().containsKey("amount"));
		assertTrue(apiError.getFieldErrors().containsKey("description"));
	}

	// -- FACTORIES

	private TransactionRequest request(String amount, String description) {
		return new TransactionRequest(new BigDecimal(amount), description);
	}

	private TransactionResponse response(long id, String amount, String description) {
		return new TransactionResponse(id, new BigDecimal(amount), description);
	}

	private List<TransactionResponse> responses(MvcResult result)
			throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(
				result.getResponse().getContentAsString(),
				new TypeReference<List<TransactionResponse>>() {
				});
	}

	private TransactionResponse response(MvcResult result) throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);
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
