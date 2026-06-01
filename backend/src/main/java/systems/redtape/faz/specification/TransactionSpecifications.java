package systems.redtape.faz.specification;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import systems.redtape.faz.dto.Currency;
import systems.redtape.faz.dto.TransactionCategory;
import systems.redtape.faz.dto.TransactionCriteria;
import systems.redtape.faz.entity.Transaction;

public final class TransactionSpecifications {

	private TransactionSpecifications() {
	}

	public static Specification<Transaction> withCriteria(TransactionCriteria criteria) {
		return Specification.allOf(
				dateFrom(criteria.getFrom()),
				dateTo(criteria.getTo()),
				descriptionContains(criteria.getDescription()),
				minAmount(criteria.getMinAmount()),
				maxAmount(criteria.getMaxAmount()),
				categoryEquals(criteria.getCategory()),
				currencyEquals(criteria.getCurrency()));
	}

	public static Specification<Transaction> dateFrom(LocalDate from) {
		return (root, query, cb) -> {
			if (from == null) {
				return null;
			}

			return cb.greaterThanOrEqualTo(root.get("date"), from.atStartOfDay());
		};
	}

	public static Specification<Transaction> dateTo(LocalDate to) {
		return (root, query, cb) -> {
			if (to == null) {
				return null;
			}

			return cb.lessThan(root.get("date"), to.plusDays(1).atStartOfDay());
		};
	}

	public static Specification<Transaction> categoryEquals(TransactionCategory category) {
		return (root, query, cb) -> {
			if (category == null) {
				return null;
			}

			return cb.equal(root.get("category"), category);
		};
	}

	public static Specification<Transaction> currencyEquals(Currency currency) {
		return (root, query, cb) -> {
			if (currency == null) {
				return null;
			}

			return cb.equal(root.get("currency"), currency);
		};
	}

	public static Specification<Transaction> descriptionContains(String description) {
		return (root, query, cb) -> {
			if (description == null || description.isBlank()) {
				return null;
			}

			return cb.like(
					cb.lower(root.get("description")),
					"%" + description.toLowerCase() + "%");
		};
	}

	public static Specification<Transaction> minAmount(BigDecimal amount) {
		return (root, query, cb) -> {
			if (amount == null) {
				return null;
			}

			return cb.greaterThanOrEqualTo(root.get("amount"), amount);
		};
	}

	public static Specification<Transaction> maxAmount(BigDecimal amount) {
		return (root, query, cb) -> {
			if (amount == null) {
				return null;
			}

			return cb.lessThanOrEqualTo(root.get("amount"), amount);
		};
	}
}
