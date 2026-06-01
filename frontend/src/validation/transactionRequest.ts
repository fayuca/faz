import { z } from "zod";
import type { TransactionRequest, TransactionRequestV2 } from "../api/generated/dtos";
import { TRANSACTION_CATEGORIES, TRANSACTION_CURRENCIES } from "../types/Transaction";
import { TransactionMessages } from "./transactionMessages";

export type { TransactionRequest, TransactionRequestV2 };

/** Planner `toLocalDateTime` / Spring JSON `LocalDateTime` (no timezone offset). */
export const LOCAL_DATE_TIME_PATTERN =
	/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/;

function isValidLocalDateTime(value: string): boolean {
	const match = value.match(
		/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})$/
	);
	if (!match) {
		return false;
	}

	const year = Number(match[1]);
	const month = Number(match[2]);
	const day = Number(match[3]);
	const hour = Number(match[4]);
	const minute = Number(match[5]);
	const second = Number(match[6]);

	if (month < 1 || month > 12 || hour > 23 || minute > 59 || second > 59) {
		return false;
	}

	const parsed = new Date(year, month - 1, day);
	return (
		parsed.getFullYear() === year &&
		parsed.getMonth() === month - 1 &&
		parsed.getDate() === day
	);
}

export const transactionRequestSchema = z.object({
	date: z
		.string()
		.min(1, TransactionMessages.dateRequired)
		.regex(LOCAL_DATE_TIME_PATTERN, TransactionMessages.dateInvalid)
		.refine(isValidLocalDateTime, TransactionMessages.dateInvalid),
	amount: z
		.number({ error: TransactionMessages.amountNumber })
		.positive(TransactionMessages.amountPositive),
	description: z
		.string()
		.trim()
		.min(1, TransactionMessages.descriptionRequired),
	category: z.enum(TRANSACTION_CATEGORIES, {
		error: TransactionMessages.categoryInvalid,
	}),
});

export const transactionRequestV2Schema = transactionRequestSchema.extend({
	currency: z.enum(TRANSACTION_CURRENCIES, {
		error: TransactionMessages.currencyInvalid,
	}),
});

export function parseTransactionRequest(input: unknown) {
	return transactionRequestSchema.safeParse(input);
}

export function parseTransactionRequestV2(input: unknown) {
	return transactionRequestV2Schema.safeParse(input);
}

export function formatZodError(error: z.ZodError): string {
	const messages = error.issues.map(issue => issue.message);
	return [...new Set(messages)].join(" ");
}

export function parseTransactionId(
	raw: string
): { ok: true; id: number } | { ok: false; message: string } {
	const trimmed = raw.trim();
	if (trimmed === "") {
		return { ok: false, message: TransactionMessages.idRequired };
	}

	const id = Number(trimmed);
	if (!Number.isInteger(id) || id <= 0) {
		return { ok: false, message: TransactionMessages.idPositive };
	}

	return { ok: true, id };
}
