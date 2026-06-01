import type { ApiError } from "./generated/dtos";
import { TransactionMessages } from "../validation/transactionMessages";

export function formatApiErrorBody(body: unknown): string {
	if (!body || typeof body !== "object") {
		return TransactionMessages.requestFailed;
	}

	const { message, fieldErrors } = body as ApiError;
	const fieldMessages = fieldErrors
		? Object.values(fieldErrors).filter(Boolean)
		: [];

	if (fieldMessages.length > 0) {
		const summary =
			typeof message === "string" && message.length > 0
				? message
				: TransactionMessages.validationSummary;
		return [summary, ...fieldMessages].join(" ");
	}

	if (typeof message === "string" && message.length > 0) {
		return message;
	}

	return TransactionMessages.requestFailed;
}
