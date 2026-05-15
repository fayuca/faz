package com.example.faz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.faz.entity.Transaction;
import com.example.faz.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	private final TransactionService service;

	public TransactionController(TransactionService service) {
		this.service = service;
	}

	@GetMapping
	public List<Transaction> getAll() {
		return service.getAll();
	}

	@PostMapping
	public Transaction create(@RequestBody Transaction transaction) {
		return service.create(transaction);
	}
}
