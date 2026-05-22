export type TransactionCategory =
	| "ENTERTAINMENT"
	| "FOOD"
	| "OTHER"
	| "TRANSPORT"
	| "UTILITIES";

export interface TransactionResponse {
	id: number;
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
