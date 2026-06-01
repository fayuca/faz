package systems.redtape.faz.constants;

public final class ValidationMessages {
	public static final String VALIDATION_SUMMARY = "Some fields need attention.";

	public static final String DATE_REQUIRED = "Date is required.";
	public static final String DATE_INVALID = "Enter a valid date and time (for example 2026-05-29T12:00:00).";

	public static final String AMOUNT_REQUIRED = "Amount is required.";
	public static final String AMOUNT_POSITIVE = "Amount must be greater than zero.";

	public static final String DESCRIPTION_REQUIRED = "Description is required.";

	public static final String FILTER_AMOUNT_MIN = "Amount must be zero or greater.";
	public static final String FILTER_AMOUNT_RANGE = "minAmount must not be greater than maxAmount.";
	public static final String FILTER_DATE_RANGE = "from must not be after to.";

	public static final String CATEGORY_REQUIRED = "Category is required.";

	public static final String CURRENCY_REQUIRED = "Currency is required.";

	public static final String BODY_UNREADABLE = "The request body could not be read.";

	private ValidationMessages() {
	}
}
