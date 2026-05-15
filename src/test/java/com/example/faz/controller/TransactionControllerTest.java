package com.example.faz.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
	void shouldCreate() throws Exception {
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
	void shouldNotCreateInvalid() throws Exception {
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
	void shouldFind() throws Exception {
		TransactionResponseDTO response = new TransactionResponseDTO(1L, new BigDecimal("100.00"), "Test");

		when(service.get(anyLong())).thenReturn(response);

		mockMvc.perform(get("/transactions/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.amount").value(100.00))
				.andExpect(jsonPath("$.description").value("Test"));
	}

	@Test
	void shouldNotFind() throws Exception {
		when(service.get(anyLong())).thenThrow(new ResourceNotFoundException("Transaction not found: -1"));

		mockMvc.perform(get("/transactions/-1"))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Transaction not found: -1"));
	}

	@Test
	void shouldDelete() throws Exception {
		doNothing().when(service).delete(anyLong());

		mockMvc.perform(delete("/transactions/1"))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void shouldNotDeleteNotFound() throws Exception {
		doThrow(new ResourceNotFoundException("Transaction not found: -1")).when(service).delete(anyLong());

		mockMvc.perform(delete("/transactions/-1"))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Transaction not found: -1"));
	}

	@Test
	void shouldUpdate() throws Exception {
		TransactionResponseDTO response = new TransactionResponseDTO(1L, new BigDecimal("200.00"), "Updated");

		when(service.update(anyLong(), any(TransactionRequestDTO.class))).thenReturn(response);

		mockMvc.perform(put("/transactions/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						    {
								"amount": 200.00,
								"description": "Updated"
						    }
						"""))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void shouldNotUpdateNotFound() throws Exception {
		when(service
				.update(anyLong(), any(TransactionRequestDTO.class)))
				.thenThrow(new ResourceNotFoundException("Transaction not found: -1"));

		mockMvc.perform(put("/transactions/-1")
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
		mockMvc.perform(put("/transactions/1")
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
}
