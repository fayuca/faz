package systems.redtape.faz.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import systems.redtape.faz.dto.TransactionCategory;
import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionResponse;
import systems.redtape.faz.entity.Transaction;
import systems.redtape.faz.exception.ApiError;

public final class ApiErrorAssertions {
	private ApiErrorAssertions() {
	}

	public static void assertApiError(ApiError apiError, String message, String... fields) {
		assertEquals(message, apiError.getMessage());
		for (String field : fields) {
			assertTrue(apiError.getFieldErrors().containsKey(field));
		}
	}
}
