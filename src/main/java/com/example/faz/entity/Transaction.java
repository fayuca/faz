package com.example.faz.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private BigDecimal amount;

	@Column
	private String description;

	@Column
	private LocalDateTime date;

	public TransactionRequest request() {
		return new TransactionRequest(
				getAmount(),
				getDescription());
	}

	public TransactionResponse response() {
		return new TransactionResponse(
				getId(),
				getAmount(),
				getDescription());
	}

	public static Transaction from(TransactionRequest request) {
		Transaction transaction = new Transaction();
		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());
		return transaction;
	}

	public static List<TransactionResponse> responses(List<Transaction> transactions) {
		return transactions.stream().map(Transaction::response).collect(Collectors.toList());
	}
}
