package systems.redtape.faz.support;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import systems.redtape.faz.dto.Currency;
import systems.redtape.faz.dto.TransactionCategory;
import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionRequestV2;
import systems.redtape.faz.entity.Transaction;

public final class TransactionTestFixtures {
	public static final LocalDateTime TEST_DATE = LocalDateTime.of(2026, 5, 26, 12, 0);

	private TransactionTestFixtures() {
	}

	public static TransactionCategory randomCategory() {
		TransactionCategory[] categories = TransactionCategory.values();
		return categories[ThreadLocalRandom.current().nextInt(categories.length)];
	}

	public static TransactionRequest request(BigDecimal amount, String description) {
		return new TransactionRequest(TEST_DATE, amount, description, randomCategory());
	}

	public static TransactionRequest request(
			LocalDateTime date,
			BigDecimal amount,
			String description,
			TransactionCategory category) {
		return new TransactionRequest(date, amount, description, category);
	}

	public static TransactionRequestV2 requestV2(BigDecimal amount, String description, Currency currency) {
		return new TransactionRequestV2(TEST_DATE, amount, description, randomCategory(), currency);
	}

	public static TransactionRequestV2 requestV2(
			LocalDateTime date,
			BigDecimal amount,
			String description,
			TransactionCategory category,
			Currency currency) {
		return new TransactionRequestV2(date, amount, description, category, currency);
	}

	public static Transaction transaction(BigDecimal amount, String description) {
		return transaction(amount, description, randomCategory(), Currency.USD);
	}

	public static Transaction transaction(
			BigDecimal amount,
			String description,
			TransactionCategory category) {
		return transaction(amount, description, category, Currency.USD);
	}

	public static Transaction transaction(
			BigDecimal amount,
			String description,
			TransactionCategory category,
			Currency currency) {
		Transaction transaction = new Transaction();
		transaction.setDate(TEST_DATE);
		transaction.setAmount(amount);
		transaction.setDescription(description);
		transaction.setCategory(category);
		transaction.setCurrency(currency);
		return transaction;
	}

	public static Transaction transaction(Long id, BigDecimal amount, String description) {
		Transaction transaction = transaction(amount, description);
		transaction.setId(id);
		return transaction;
	}

	public static Transaction transaction(
			Long id,
			BigDecimal amount,
			String description,
			TransactionCategory category,
			Currency currency) {
		Transaction transaction = transaction(amount, description, category, currency);
		transaction.setId(id);
		return transaction;
	}

	public static BigDecimal amount(int i) {
		return new BigDecimal(i * 100);
	}

	public static String description(int i) {
		return "Transaction " + i;
	}
}
