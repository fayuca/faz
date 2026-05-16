package com.example.faz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.entity.Transaction;
import com.example.faz.exception.ApiErrors;
import com.example.faz.exception.ResourceNotFoundException;
import com.example.faz.repository.TransactionRepository;

@Service
public class TransactionService {
	private final TransactionRepository repository;

	public TransactionService(TransactionRepository repository) {
		this.repository = repository;
	}

	public TransactionResponse create(TransactionRequest request) {
		Transaction transaction = Transaction.from(request);
		Transaction saved = repository.save(transaction);
		return saved.response();
	}

	public void delete(Long id) {
		repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ApiErrors.notFound(id)));
		repository.deleteById(id);
	}

	public TransactionResponse get(Long id) throws ResourceNotFoundException {
		Transaction saved = repository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ApiErrors.notFound(id)));

		return saved.response();
	}

	public List<TransactionResponse> getAll(String description) {
		return description == null || description.isBlank()
				? Transaction.responses(repository.findAll())
				: Transaction.responses(repository.findByDescriptionContainingIgnoreCase(description));
	}

	public TransactionResponse update(Long id, TransactionRequest request) {
		Transaction transaction = repository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ApiErrors.notFound(id)));

		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());

		Transaction saved = repository.save(transaction);
		return saved.response();
	}
}
