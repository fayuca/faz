package com.example.faz.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	private final TransactionService service;

	public TransactionController(TransactionService service) {
		this.service = service;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	@GetMapping("/{id}")
	public TransactionResponse get(@PathVariable Long id) {
		return service.get(id);
	}

	@GetMapping
	public List<TransactionResponse> getAll(@RequestParam(required = false) String description) {
		return service.getAll(description);
	}

	@PostMapping
	public TransactionResponse post(@RequestBody @Valid TransactionRequest dto) {
		return service.create(dto);
	}

	@PutMapping("/{id}")
	public TransactionResponse put(@PathVariable Long id, @RequestBody @Valid TransactionRequest dto) {
		return service.update(id, dto);
	}
}
