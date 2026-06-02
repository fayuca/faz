package systems.redtape.faz.constants;

import systems.redtape.faz.dto.TransactionCategory;

/** Example JSON for docs/tests — built from domain enums, not duplicated literals. */
public final class OpenApiExamples {
	private static final String EXAMPLE_DATE = "2026-05-26T12:00:00";
	private static final String EXAMPLE_DESCRIPTION = "Lunch";

	public static final String TRANSACTION_RESPONSE_V1 = """
			{
			  "id": 1,
			  "date": "%s",
			  "amount": 100.00,
			  "description": "%s",
			  "category": "%s"
			}
			"""
			.formatted(EXAMPLE_DATE, EXAMPLE_DESCRIPTION, TransactionCategory.FOOD.name());

	public static final String TRANSACTION_RESPONSE_V2 = """
			{
			  "id": 1,
			  "date": "%s",
			  "amount": 100.00,
			  "description": "%s",
			  "category": "%s",
			  "currency": "%s"
			}
			"""
			.formatted(
					EXAMPLE_DATE,
					EXAMPLE_DESCRIPTION,
					TransactionCategory.FOOD.name(),
					FazDefaults.BOOK_CURRENCY.name());

	private OpenApiExamples() {
	}
}
