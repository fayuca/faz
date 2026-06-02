package systems.redtape.faz.constants;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import systems.redtape.faz.dto.TransactionCategory;

class OpenApiExamplesTest {
	@Test
	void examplesTrackDomainEnums() {
		assertTrue(OpenApiExamples.TRANSACTION_RESPONSE_V1.contains(TransactionCategory.FOOD.name()));
		assertTrue(OpenApiExamples.TRANSACTION_RESPONSE_V2.contains(FazDefaults.BOOK_CURRENCY.name()));
	}
}
