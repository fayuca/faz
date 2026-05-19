package com.example.faz.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.example.faz.dto.TransactionCategory;
import com.example.faz.dto.TransactionCriteria;
import com.example.faz.entity.Transaction;

public final class TransactionSpecifications {

	private TransactionSpecifications() {
	}

	public static Specification<Transaction> withCriteria(TransactionCriteria criteria) {
		return Specification.allOf(
				descriptionContains(criteria.getDescription()),
				minAmount(criteria.getMinAmount()),
				maxAmount(criteria.getMaxAmount()),
				categoryEquals(criteria.getCategory()));
	}

	public static Specification<Transaction> categoryEquals(TransactionCategory category) {
		return (root, query, cb) -> {
			if (category == null) {
				return null;
			}

			return cb.equal(root.get("category"), category);
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
