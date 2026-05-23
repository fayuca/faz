import type { Page, TransactionRequest, TransactionResponse } from "../types/Transaction";
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

export async function createTransaction(
	request: TransactionRequest
): Promise<TransactionResponse> {
	const response = await http.post<TransactionResponse>(
		"/transactions",
		request
	);

	return response.data;
}

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
