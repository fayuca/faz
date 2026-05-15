package com.example.faz.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.faz.entity.Transaction;
import com.example.faz.repository.TransactionRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransactionRepository repository;

	@Test
	void shouldCreateTransactionEndToEnd() throws Exception {
		mockMvc.perform(post("/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
						        "amount": 100.00,
						        "description": "Integration Test"
						    }
						"""))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.amount").value(100.00))
				.andExpect(jsonPath("$.description").value("Integration Test"));

		List<Transaction> transactions = repository.findAll();

		assertEquals(1, transactions.size());

		Transaction saved = transactions.getFirst();

		assertEquals("Integration Test", saved.getDescription());
		assertEquals(0, saved.getAmount().compareTo(new BigDecimal("100.00")));
	}
}
