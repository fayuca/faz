package com.example.faz.dto;

import java.math.BigDecimal;

public class TransactionResponseDTO {
	private Long id;
	private BigDecimal amount;
	private String description;

	public TransactionResponseDTO() {
	}

	public TransactionResponseDTO(Long id, BigDecimal amount, String description) {
		this.id = id;
		this.amount = amount;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}
}
