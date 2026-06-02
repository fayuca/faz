package systems.redtape.faz.constants;

import systems.redtape.faz.dto.Currency;

/** Domain defaults — single source for business rules applied server-side. */
public final class FazDefaults {
	public static final Currency BOOK_CURRENCY = Currency.USD;

	private FazDefaults() {
	}
}
