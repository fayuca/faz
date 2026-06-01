package systems.redtape.faz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import systems.redtape.faz.constants.ValidationMessages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "Optional filters for listing transactions.")
public class TransactionCriteria {
	@Schema(description = "Include transactions on or after this date.", example = "2026-05-01", type = "string", format = "date")
	private LocalDate from;

	@Schema(description = "Include transactions on or before this date.", example = "2026-05-31", type = "string", format = "date")
	private LocalDate to;

	@Schema(description = "Filter by description (contains).")
	private String description;

	@DecimalMin(value = "0", message = ValidationMessages.FILTER_AMOUNT_MIN)
	@Schema(description = "Minimum transaction amount.", example = "10.00", minimum = "0")
	private BigDecimal minAmount;

	@DecimalMin(value = "0", message = ValidationMessages.FILTER_AMOUNT_MIN)
	@Schema(description = "Maximum transaction amount.", example = "100.00", minimum = "0")
	private BigDecimal maxAmount;

	@Schema(description = "Filter by category.", implementation = TransactionCategory.class)
	private TransactionCategory category;

	@Schema(description = "Filter by currency (v2).", implementation = Currency.class)
	private Currency currency;

	@AssertTrue(message = ValidationMessages.FILTER_AMOUNT_RANGE)
	public boolean isAmountRangeValid() {
		if (minAmount == null || maxAmount == null) {
			return true;
		}

		return minAmount.compareTo(maxAmount) <= 0;
	}

	@AssertTrue(message = ValidationMessages.FILTER_DATE_RANGE)
	public boolean isDateRangeValid() {
		if (from == null || to == null) {
			return true;
		}

		return !from.isAfter(to);
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

	public TransactionCategory getCategory() {
		return category;
	}

	public void setCategory(TransactionCategory category) {
		this.category = category;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

}
