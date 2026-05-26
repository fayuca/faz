
export const TRANSACTION_CATEGORIES = [
	"ENTERTAINMENT",
	"FOOD",
	"TRANSPORT",
	"UTILITIES",
	"OTHER"
] as const;
export type TransactionCategory = typeof TRANSACTION_CATEGORIES[number];

export interface TransactionRequest {
	date: string;
	amount: number;
	description: string;
	category: TransactionCategory;
}

export interface TransactionResponse {
	id: number;
	date: string;
	amount: number;
	description: string;
	category: TransactionCategory;
}

export interface Page<T> {
	content: T[];
	number: number;
	size: number;
	totalElements: number;
	totalPages: number;
}
