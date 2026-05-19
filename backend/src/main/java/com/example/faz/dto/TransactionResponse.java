package com.example.faz.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = """
		{
		  "id": 1,
		  "amount": 100.00,
		  "description": "Lunch",
		  "category": "FOOD"
		}
		""")
public class TransactionResponse {
	private Long id;
	private BigDecimal amount;
	private String description;
	private TransactionCategory category;

	public TransactionResponse(Long id, BigDecimal amount, String description, TransactionCategory category) {
		this.id = id;
		this.amount = amount;
		this.description = description;
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
