package com.example.faz.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.faz.dto.TransactionRequestDTO;
import com.example.faz.dto.TransactionResponseDTO;

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

	public TransactionRequestDTO toRequestDTO() {
		return new TransactionRequestDTO(
				getAmount(),
				getDescription());
	}

	public TransactionResponseDTO toResponseDTO() {
		return new TransactionResponseDTO(
				getId(),
				getAmount(),
				getDescription());
	}

	public static Transaction fromRequestDTO(TransactionRequestDTO request) {
		Transaction transaction = new Transaction();
		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());
		return transaction;
	}
}
