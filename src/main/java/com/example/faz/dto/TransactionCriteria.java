package com.example.faz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionCriteria {
	private String description;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private LocalDate from;
	private LocalDate to;
	private TransactionCategory category;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public LocalDate getFrom() {
		return from;
	}

	public void setFrom(LocalDate from) {
		this.from = from;
	}

	public LocalDate getTo() {
		return to;
	}

	public void setTo(LocalDate to) {
		this.to = to;
	}

	public TransactionCategory getCategory() {
		return category;
	}

	public void setCategory(TransactionCategory category) {
		this.category = category;
	}

}
