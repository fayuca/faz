package com.example.faz.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
	void shouldCreate() throws Exception {
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

	@Test
	void shouldUpdate() throws Exception {
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("100.00"));
		transaction.setDescription("Old");

		Transaction saved = repository.save(transaction);

		mockMvc.perform(put("/transactions/" + saved.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
						        "amount": 200.00,
						        "description": "Updated"
						    }
						"""))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(200.00))
				.andExpect(jsonPath("$.description").value("Updated"));

		Transaction updated = repository.findById(saved.getId()).orElseThrow();

		assertEquals(0, updated.getAmount().compareTo(new BigDecimal("200.00")));
		assertEquals("Updated", updated.getDescription());
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		mockMvc.perform(put("/transactions/" + -1)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
						        "amount": 200.00,
						        "description": "Updated"
						    }
						"""))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Transaction not found: -1"));
	}

	@Test
	void shouldNotUpdateInvalid() throws Exception {
		mockMvc.perform(put("/transactions/" + 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
						        "amount": -10.0,
						        "description": null
						    }
						"""))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation failed"))
				.andExpect(jsonPath("$.fieldErrors.amount").exists())
				.andExpect(jsonPath("$.fieldErrors.description").exists());
	}
}
