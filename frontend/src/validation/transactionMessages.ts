/** User-facing copy — keep aligned with backend `ValidationMessages`. */
export const TransactionMessages = {
	validationSummary: "Some fields need attention.",

	dateRequired: "Date is required.",
	dateInvalid: "Enter a valid date and time (for example 2026-05-29T12:00:00).",

	amountNumber: "Amount must be a number.",
	amountPositive: "Amount must be greater than zero.",

	descriptionRequired: "Description is required.",

	filterDateRange: "From must not be after to.",
	filterAmountRange: "Min amount must not be greater than max amount.",

	categoryInvalid: "Choose a valid category.",

	currencyInvalid: "Choose a valid currency (USD or EUR).",

	jsonInvalid: "The request body is not valid JSON.",

	idRequired: "Transaction ID is required.",
	idPositive: "Transaction ID must be a positive whole number.",

	requestFailed: "The request could not be completed. Please try again.",
} as const;
