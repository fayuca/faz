package com.example.faz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.faz.dto.TransactionRequestDTO;
import com.example.faz.dto.TransactionResponseDTO;
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
		return service.getAll();
	}

	@GetMapping("/{id}")
	public TransactionResponseDTO getById(@PathVariable Long id) {
		return service.getById(id);
	}

	@PostMapping
	public TransactionResponseDTO create(@RequestBody @Valid TransactionRequestDTO dto) {
		return service.create(dto);
	}
}
