package systems.redtape.faz.controller;

import static systems.redtape.faz.support.ApiErrorAssertions.assertApiError;
import static systems.redtape.faz.support.MockMvcJsonSupport.assertTransactionResponse;
import static systems.redtape.faz.support.MockMvcJsonSupport.doDelete;
import static systems.redtape.faz.support.MockMvcJsonSupport.doGet;
import static systems.redtape.faz.support.MockMvcJsonSupport.doPost;
import static systems.redtape.faz.support.MockMvcJsonSupport.doPut;
import static systems.redtape.faz.support.MockMvcJsonSupport.apiError;
import static systems.redtape.faz.support.MockMvcJsonSupport.transactionPageContent;
import static systems.redtape.faz.support.MockMvcJsonSupport.transactionResponse;
import static systems.redtape.faz.support.TransactionTestFixtures.TEST_DATE;
import static systems.redtape.faz.support.TransactionTestFixtures.randomCategory;
import static systems.redtape.faz.support.TransactionTestFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import systems.redtape.faz.constants.ApiInfo;
import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.dto.TransactionCriteria;
import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionResponse;
import systems.redtape.faz.exception.ResourceNotFoundException;
import systems.redtape.faz.service.TransactionService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
	private static final String TRANSACTIONS = ApiPaths.V1_TRANSACTIONS;

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
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		when(service.create(any(TransactionRequest.class)))
				.thenReturn(stubResponse(id, amount, description));

		MvcResult result = doPost(mockMvc, objectMapper, TRANSACTIONS, request(amount, description))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		assertTransactionResponse(transactionResponse(objectMapper, result), id, amount, description, TEST_DATE);
	}

	@Test
	void shouldNotCreateInvalid() throws Exception {
		BigDecimal amount = new BigDecimal("-10.00");

		MvcResult result = doPost(
				mockMvc,
				objectMapper,
				TRANSACTIONS,
				new TransactionRequest(null, amount, null, randomCategory()))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();

		assertApiError(
				apiError(objectMapper, result),
				ApiInfo.ERR_VALIDATION_FAILED,
				"date",
				"amount",
				"description");
	}

	@Test
	void shouldFind() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		when(service.get(id)).thenReturn(stubResponse(id, amount, description));

		MvcResult result = doGet(mockMvc, TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		assertTransactionResponse(transactionResponse(objectMapper, result), id, amount, description, TEST_DATE);
	}

	@Test
	void shouldFindByCriteria() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";

		when(service.getAll(any(TransactionCriteria.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(stubResponse(id, amount, description))));

		MvcResult result = doGet(mockMvc, TRANSACTIONS + "?description=" + description)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<TransactionResponse> responses = transactionPageContent(objectMapper, result);
		assertEquals(1, responses.size());
		assertTransactionResponse(responses.getFirst(), id, amount, description, TEST_DATE);
	}

	@Test
	void shouldFindAllPaged() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("100.00");
		String description = "Test";
		int page = 0;
		int size = 10;

		when(service.getAll(any(TransactionCriteria.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(stubResponse(id, amount, description))));

		MvcResult result = doGet(mockMvc, TRANSACTIONS + "?page=" + page + "&size=" + size)
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<TransactionResponse> responses = transactionPageContent(objectMapper, result);
		assertEquals(1, responses.size());
		assertTransactionResponse(responses.getFirst(), id, amount, description, TEST_DATE);
	}

	@Test
	void shouldNotFind() throws Exception {
		Long id = -1L;
		String message = ApiInfo.notFound(id);

		when(service.get(id)).thenThrow(new ResourceNotFoundException(message));

		MvcResult result = doGet(mockMvc, TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		assertApiError(apiError(objectMapper, result), message);
	}

	@Test
	void shouldDelete() throws Exception {
		Long id = 1L;

		doNothing().when(service).delete(id);

		doDelete(mockMvc, TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isNoContent());

		verify(service).delete(id);
	}

	@Test
	void shouldNotDeleteNotFound() throws Exception {
		Long id = -1L;
		String message = ApiInfo.notFound(id);

		doThrow(new ResourceNotFoundException(message)).when(service).delete(id);

		MvcResult result = doDelete(mockMvc, TRANSACTIONS + "/" + id)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		assertApiError(apiError(objectMapper, result), message);
	}

	@Test
	void shouldUpdate() throws Exception {
		Long id = 1L;
		BigDecimal amount = new BigDecimal("200.00");
		String description = "Updated";

		when(service.update(eq(id), any(TransactionRequest.class)))
				.thenReturn(stubResponse(id, amount, description));

		MvcResult result = doPut(mockMvc, objectMapper, TRANSACTIONS + "/" + id, request(amount, description))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		assertTransactionResponse(transactionResponse(objectMapper, result), id, amount, description, TEST_DATE);
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		Long id = -1L;
		BigDecimal amount = new BigDecimal("200.00");
		String description = "Updated";
		String message = ApiInfo.notFound(id);

		when(service.update(eq(id), any(TransactionRequest.class)))
				.thenThrow(new ResourceNotFoundException(message));

		MvcResult result = doPut(mockMvc, objectMapper, TRANSACTIONS + "/" + id, request(amount, description))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		assertApiError(apiError(objectMapper, result), message);
	}

	@Test
	void shouldNotUpdateInvalid() throws Exception {
		MvcResult result = doPut(
				mockMvc,
				objectMapper,
				TRANSACTIONS + "/-1",
				new TransactionRequest(null, new BigDecimal("-10.00"), null, randomCategory()))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();

		assertApiError(
				apiError(objectMapper, result),
				ApiInfo.ERR_VALIDATION_FAILED,
				"date",
				"amount",
				"description");
	}

	// -- FACTORIES

	private TransactionResponse stubResponse(Long id, BigDecimal amount, String description) {
		return new TransactionResponse(id, TEST_DATE, amount, description, randomCategory());
	}
}
