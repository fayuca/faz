package systems.redtape.faz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
	private Long id;
	private LocalDateTime date;
	private BigDecimal amount;
	private String description;
	private TransactionCategory category;

	public TransactionResponse(Long id, LocalDateTime date, BigDecimal amount, String description,
			TransactionCategory category) {
		this.id = id;
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
