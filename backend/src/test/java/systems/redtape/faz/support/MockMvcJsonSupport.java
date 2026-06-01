package systems.redtape.faz.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionResponse;
import systems.redtape.faz.entity.Transaction;
import systems.redtape.faz.exception.ApiError;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public final class MockMvcJsonSupport {
	private MockMvcJsonSupport() {
	}

	public static ResultActions doGet(MockMvc mockMvc, String url) throws Exception {
		return mockMvc.perform(get(url));
	}

	public static ResultActions doDelete(MockMvc mockMvc, String url) throws Exception {
		return mockMvc.perform(delete(url));
	}

	public static ResultActions doPost(MockMvc mockMvc, ObjectMapper objectMapper, String url, TransactionRequest request)
			throws Exception {
		return doPostJson(mockMvc, objectMapper, url, request);
	}

	public static ResultActions doPostJson(MockMvc mockMvc, ObjectMapper objectMapper, String url, Object body)
			throws Exception {
		return mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)));
	}

	public static ResultActions doPut(MockMvc mockMvc, ObjectMapper objectMapper, String url, TransactionRequest request)
			throws Exception {
		return mockMvc.perform(put(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)));
	}

	public static ApiError apiError(ObjectMapper objectMapper, MvcResult result)
			throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);
	}

	public static TransactionResponse transactionResponse(ObjectMapper objectMapper, MvcResult result)
			throws JacksonException, UnsupportedEncodingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);
	}

	public static List<TransactionResponse> transactionPageContent(ObjectMapper objectMapper, MvcResult result)
			throws Exception {
		JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

		return objectMapper.readValue(
				root.get("content").toString(),
				new TypeReference<List<TransactionResponse>>() {
				});
	}

	public static void assertTransactionResponse(
			TransactionResponse response,
			Long id,
			BigDecimal amount,
			String description,
			LocalDateTime date) {
		assertEquals(id, response.getId());
		assertEquals(0, amount.compareTo(response.getAmount()));
		assertEquals(description, response.getDescription());
		assertEquals(date, response.getDate());
	}

	public static void assertTransactionResponse(
			TransactionResponse response,
			BigDecimal amount,
			String description,
			LocalDateTime date) {
		assertEquals(0, amount.compareTo(response.getAmount()));
		assertEquals(description, response.getDescription());
		assertEquals(date, response.getDate());
	}

	public static void assertTransactionEntity(
			Transaction transaction,
			Long id,
			BigDecimal amount,
			String description,
			LocalDateTime date) {
		assertEquals(id, transaction.getId());
		assertEquals(0, amount.compareTo(transaction.getAmount()));
		assertEquals(description, transaction.getDescription());
		assertEquals(date, transaction.getDate());
	}
}
