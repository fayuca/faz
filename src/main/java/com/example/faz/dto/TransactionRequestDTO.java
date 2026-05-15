package com.example.faz.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransactionRequestDTO {
	@NotNull
	@Positive
	private BigDecimal amount;

	@NotNull
	private String description;

	public TransactionRequestDTO(BigDecimal amount, String description) {
		this.amount = amount;
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
