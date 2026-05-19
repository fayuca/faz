package com.example.faz.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(example = """
		{
		  "amount": 100.00,
		  "description": "Lunch",
		  "category": "FOOD"
		}
		""")
public class TransactionRequest {
	@NotNull
	@Positive
	private BigDecimal amount;

	@NotNull
	private String description;

	@NotNull
	private TransactionCategory category;

	public TransactionRequest(BigDecimal amount, String description, TransactionCategory category) {
		this.amount = amount;
		this.description = description;
		this.category = category;
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

	public TransactionCategory getCategory() {
		return category;
	}

	public void setCategory(TransactionCategory category) {
		this.category = category;
	}
}
