package com.example.faz.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.faz.dto.TransactionRequestDTO;
import com.example.faz.dto.TransactionResponseDTO;
import com.example.faz.entity.Transaction;
import com.example.faz.exception.ResourceNotFoundException;
import com.example.faz.repository.TransactionRepository;

@Service
public class TransactionService {
	private final TransactionRepository repository;

	public TransactionService(TransactionRepository repository) {
		this.repository = repository;
	}

	public TransactionResponseDTO create(TransactionRequestDTO request) {
		Transaction transaction = Transaction.fromRequestDTO(request);
		Transaction saved = repository.save(transaction);
		return saved.toResponseDTO();
	}

	public void deleteById(Long id) {
		repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
		repository.deleteById(id);
	}

	public List<TransactionResponseDTO> getAll() {
		return repository.findAll().stream().map(Transaction::toResponseDTO).collect(Collectors.toList());
	}

	public TransactionResponseDTO getById(Long id) throws ResourceNotFoundException {
		Transaction saved = repository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));

		return saved.toResponseDTO();
	}
}
