package com.example.faz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.faz.dto.TransactionRequestDTO;
import com.example.faz.dto.TransactionResponseDTO;
import com.example.faz.entity.Transaction;
import com.example.faz.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	private final TransactionService service;

	public TransactionController(TransactionService service) {
		this.service = service;
	}

	@GetMapping
	public List<TransactionResponseDTO> getAll() {
		return service.getAll().stream()
				.map(t -> new TransactionResponseDTO(
						t.getId(),
						t.getAmount(),
						t.getDescription()))
				.toList();
	}

	@PostMapping
	public TransactionResponseDTO create(@RequestBody @Valid TransactionRequestDTO dto) {
		Transaction transaction = new Transaction();
		transaction.setAmount(dto.getAmount());
		transaction.setDescription(dto.getDescription());

		Transaction saved = service.create(transaction);

		return new TransactionResponseDTO(
				saved.getId(),
				saved.getAmount(),
				saved.getDescription());
	}
}
