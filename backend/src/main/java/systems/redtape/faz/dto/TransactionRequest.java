package systems.redtape.faz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import systems.redtape.faz.constants.ValidationMessages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Fields required to create or update a transaction.")
public class TransactionRequest {
	@NotNull(message = ValidationMessages.DATE_REQUIRED)
	@Schema(
			description = "Transaction date and time (no timezone offset).",
			example = "2026-05-29T12:00:00",
			type = "string",
			format = "date-time")
	private LocalDateTime date;

	@NotNull(message = ValidationMessages.AMOUNT_REQUIRED)
	@Positive(message = ValidationMessages.AMOUNT_POSITIVE)
	@Schema(description = "Transaction amount.", example = "100.00", minimum = "0", exclusiveMinimum = true)
	private BigDecimal amount;

	@NotBlank(message = ValidationMessages.DESCRIPTION_REQUIRED)
	@Schema(description = "Short description.", example = "Lunch", minLength = 1)
	private String description;

	@NotNull(message = ValidationMessages.CATEGORY_REQUIRED)
	@Schema(description = "Spending category.", implementation = TransactionCategory.class)
	private TransactionCategory category;

	public TransactionRequest(LocalDateTime date, BigDecimal amount, String description, TransactionCategory category) {
		this.date = date;
		this.amount = amount;
		this.description = description;
		this.category = category;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
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
