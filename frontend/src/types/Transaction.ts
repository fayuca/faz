export type {
	Currency,
	TransactionCategory,
	TransactionRequest,
	TransactionRequestV2,
	TransactionResponse,
	TransactionResponseV2,
} from "../api/generated/dtos";

import type {
	Currency,
	TransactionCategory,
	TransactionResponse,
} from "../api/generated/dtos";

export const TRANSACTION_CATEGORIES = [
	"ENTERTAINMENT",
	"FOOD",
	"TRANSPORT",
	"UTILITIES",
	"OTHER",
] as const satisfies readonly TransactionCategory[];

export const TRANSACTION_CURRENCIES = ["USD", "EUR"] as const satisfies readonly Currency[];

/** JPA / Spring `sort` query properties (entity field names). */
export const TRANSACTION_SORT_PROPERTIES = [
	"id",
	"date",
	"amount",
	"description",
	"category",
] as const satisfies readonly (keyof TransactionResponse)[];

export type TransactionSortProperty =
	(typeof TRANSACTION_SORT_PROPERTIES)[number];

export const TRANSACTION_SORT_DIRECTIONS = ["asc", "desc"] as const;

export type TransactionSortDirection =
	(typeof TRANSACTION_SORT_DIRECTIONS)[number];

export function formatTransactionSort(
	property: TransactionSortProperty | "",
	direction: TransactionSortDirection
): string | undefined {
	if (property === "") {
		return undefined;
	}

	return `${property},${direction}`;
}

export function isTransactionSortProperty(
	value: string
): value is TransactionSortProperty {
	return (TRANSACTION_SORT_PROPERTIES as readonly string[]).includes(value);
}

export function isTransactionSortDirection(
	value: string
): value is TransactionSortDirection {
	return (TRANSACTION_SORT_DIRECTIONS as readonly string[]).includes(value);
}
