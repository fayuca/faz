package com.example.faz.dto;

import java.math.BigDecimal;

public class TransactionRequestDTO {
	private BigDecimal amount;
	private String description;

	public TransactionRequestDTO() {
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
