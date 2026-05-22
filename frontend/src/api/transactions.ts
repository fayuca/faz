import type { Page, TransactionResponse } from "../types/Transaction";
import { http } from "./http";

export type TransactionCriteria = {
	description?: string;
	minAmount?: number;
	maxAmount?: number;
	category?: string;
	page?: number;
	size?: number;
	sort?: string;
};

export async function getTransactions(
	params: TransactionCriteria
): Promise<Page<TransactionResponse>> {
	const response = await http.get<Page<TransactionResponse>>(
		"/transactions",
		{
			params,
		}
	);

	return response.data;
}
