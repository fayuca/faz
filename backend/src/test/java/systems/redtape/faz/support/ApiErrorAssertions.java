package systems.redtape.faz.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
