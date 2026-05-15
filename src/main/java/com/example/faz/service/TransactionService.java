package com.example.faz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.faz.entity.Transaction;
import com.example.faz.repository.TransactionRepository;

@Service
public class TransactionService {
	private final TransactionRepository repository;

	public TransactionService(TransactionRepository repository) {
		this.repository = repository;
	}

	public Transaction create(Transaction transaction) {
		return repository.save(transaction);
	}

	public List<Transaction> getAll() {
		return repository.findAll();
	}
}
