package com.example.faz.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.faz.dto.TransactionCategory;
import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime date;

	// -

	@Column(nullable = false)
	private BigDecimal amount;

	@Column
	private String description;

	@Enumerated(EnumType.STRING)
	private TransactionCategory category;

	// -

	public TransactionRequest request() {
		return new TransactionRequest(
				getDate(),
				getAmount(),
				getDescription(),
				getCategory());
	}

	public TransactionResponse response() {
		return new TransactionResponse(
				getId(),
				getDate(),
				getAmount(),
				getDescription(),
				getCategory());
	}

	public static Transaction from(TransactionRequest request) {
		Transaction transaction = new Transaction();
		transaction.setDate(request.getDate());
		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());
		transaction.setCategory(request.getCategory());
		return transaction;
	}

	public static List<TransactionResponse> responses(List<Transaction> transactions) {
		return transactions.stream().map(Transaction::response).collect(Collectors.toList());
	}
}
