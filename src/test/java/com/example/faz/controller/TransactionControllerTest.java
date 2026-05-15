package com.example.faz.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import com.example.faz.dto.TransactionRequestDTO;
import com.example.faz.dto.TransactionResponseDTO;
import com.example.faz.exception.ResourceNotFoundException;
import com.example.faz.service.TransactionService;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TransactionService service;

	@Test
	void shouldCreateTransaction() throws Exception {
		TransactionResponseDTO dto = new TransactionResponseDTO(1L, new BigDecimal("100.00"), "Test");

		when(service.create(any(TransactionRequestDTO.class))).thenReturn(dto);

		mockMvc.perform(post("/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
						        "amount": 100.00,
						        "description": "Test"
						    }
						"""))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.amount").value(100.00))
				.andExpect(jsonPath("$.description").value("Test"));
	}

	@Test
	void shouldReturnAllTransactions() throws Exception {
		List<TransactionResponseDTO> mockList = List.of();

		when(service.getAll()).thenReturn(mockList);

		mockMvc.perform(get("/transactions"))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void shouldRejectInvalidTransaction() throws Exception {
		mockMvc.perform(post("/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
						        "amount": -10,
						        "description": null
						    }
						"""))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation failed"))
				.andExpect(jsonPath("$.fieldErrors.amount").exists())
				.andExpect(jsonPath("$.fieldErrors.description").exists());
	}

	@Test
	void shouldFindTransactionById() throws Exception {
		TransactionResponseDTO response = new TransactionResponseDTO(1L, new BigDecimal("100.00"), "Test");

		when(service.getById(anyLong())).thenReturn(response);

		mockMvc.perform(get("/transactions/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.amount").value(100.00))
				.andExpect(jsonPath("$.description").value("Test"));
	}

	@Test
	void shouldNotFindTransactionById() throws Exception {
		when(service.getById(anyLong())).thenThrow(new ResourceNotFoundException("Transaction not found: 1"));

		mockMvc.perform(get("/transactions/1"))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Transaction not found: 1"));
	}

	@Test
	void shouldDeleteTransactionById() throws Exception {
		doNothing().when(service).deleteById(anyLong());

		mockMvc.perform(delete("/transactions/1"))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void shouldFailToDeleteTransactionById() throws Exception {
		doThrow(new ResourceNotFoundException("Transaction not found: 1")).when(service).deleteById(anyLong());

		mockMvc.perform(delete("/transactions/1"))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Transaction not found: 1"));
	}
}
