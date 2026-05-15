package com.example.faz.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.faz.entity.Transaction;
import com.example.faz.service.TransactionService;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TransactionService service;

	@Test
	void shouldCreateTransaction() throws Exception {
		Transaction entity = new Transaction();
		entity.setId(1L);
		entity.setAmount(new BigDecimal("100.00"));
		entity.setDescription("Test");

		when(service.create(any(Transaction.class)))
				.thenReturn(entity);

		mockMvc.perform(post("/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
						        "amount": 100.00,
						        "description": "Test"
						    }
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.amount").value(100.00))
				.andExpect(jsonPath("$.description").value("Test"));
	}

	@Test
	void shouldReturnAllTransactions() throws Exception {
		List<com.example.faz.entity.Transaction> mockList = List.of();

		when(service.getAll()).thenReturn(mockList);

		mockMvc.perform(get("/transactions"))
				.andExpect(status().isOk());
	}
}
