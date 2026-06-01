package systems.redtape.faz.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import systems.redtape.faz.dto.Currency;
import systems.redtape.faz.dto.TransactionCategory;
import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionRequestV2;
import systems.redtape.faz.dto.TransactionResponse;
import systems.redtape.faz.dto.TransactionResponseV2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private LocalDateTime date;

	// -

	@Column(nullable = false)
	private BigDecimal amount;

	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionCategory category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Currency currency = Currency.USD;

	// -

	public TransactionRequest request() {
		return new TransactionRequest(
				getDate(),
				getAmount(),
				getDescription(),
				getCategory());
	}

	public TransactionResponse response() {
		return new TransactionResponse(
				getId(),
				getDate(),
				getAmount(),
				getDescription(),
				getCategory());
	}

	public TransactionResponseV2 responseV2() {
		return new TransactionResponseV2(
				getId(),
				getDate(),
				getAmount(),
				getDescription(),
				getCategory(),
				getCurrency());
	}

	public static Transaction from(TransactionRequest request) {
		Transaction transaction = new Transaction();
		transaction.setDate(request.getDate());
		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());
		transaction.setCategory(request.getCategory());
		transaction.setCurrency(Currency.USD);
		return transaction;
	}

	public static Transaction fromV2(TransactionRequestV2 request) {
		Transaction transaction = new Transaction();
		transaction.setDate(request.getDate());
		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());
		transaction.setCategory(request.getCategory());
		transaction.setCurrency(request.getCurrency());
		return transaction;
	}

	public void applyV2(TransactionRequestV2 request) {
		setDate(request.getDate());
		setAmount(request.getAmount());
		setDescription(request.getDescription());
		setCategory(request.getCategory());
		setCurrency(request.getCurrency());
	}

	public static List<TransactionResponse> responses(List<Transaction> transactions) {
		return transactions.stream().map(Transaction::response).collect(Collectors.toList());
	}
}
