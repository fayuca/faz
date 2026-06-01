import type {
	TransactionListParams,
	TransactionPage,
	TransactionPageV2,
	TransactionRequest,
	TransactionRequestV2,
	TransactionResponse,
	TransactionResponseV2,
} from "./generated/dtos";
import { http } from "./http";
import {
	apiVersionBase,
	DEFAULT_API_VERSION,
	TRANSACTIONS_PATH,
	type ApiVersion,
} from "./paths";

export type { ApiVersion } from "./paths";
export type {
	Currency,
	TransactionListParams,
	TransactionPage,
	TransactionPageV2,
	TransactionRequest,
	TransactionRequestV2,
	TransactionResponse,
	TransactionResponseV2,
} from "./generated/dtos";

/** List query params (OpenAPI criteria + pagination / sort). */
export type TransactionCriteria = TransactionListParams;

export type TransactionApiScope = {
	version: ApiVersion;
	resourcePath: string;
};

export type HttpResult = {
	status: number;
	body: unknown;
};

function resolveScope(scope?: TransactionApiScope): TransactionApiScope {
	return (
		scope ?? {
			version: DEFAULT_API_VERSION,
			resourcePath: TRANSACTIONS_PATH,
		}
	);
}

export async function createTransaction(
	request: TransactionRequest,
	scope?: TransactionApiScope
): Promise<TransactionResponse>;
export async function createTransaction(
	request: TransactionRequestV2,
	scope: TransactionApiScope & { version: "v2" }
): Promise<TransactionResponseV2>;
export async function createTransaction(
	request: TransactionRequest | TransactionRequestV2,
	scope?: TransactionApiScope
): Promise<TransactionResponse | TransactionResponseV2> {
	const { version, resourcePath } = resolveScope(scope);

	if (version === "v2") {
		const response = await http.post<TransactionResponseV2>(
			resourcePath,
			request,
			{ baseURL: apiVersionBase(version) }
		);
		return response.data as TransactionResponseV2;
	}

	const response = await http.post<TransactionResponse>(
		resourcePath,
		request,
		{ baseURL: apiVersionBase(version) }
	);

	return response.data as TransactionResponse;
}

export async function getTransactions(
	params: TransactionListParams,
	scope?: TransactionApiScope
): Promise<TransactionPage>;
export async function getTransactions(
	params: TransactionListParams,
	scope: TransactionApiScope & { version: "v2" }
): Promise<TransactionPageV2>;
export async function getTransactions(
	params: TransactionListParams,
	scope?: TransactionApiScope
): Promise<TransactionPage | TransactionPageV2> {
	const { version, resourcePath } = resolveScope(scope);

	if (version === "v2") {
		const response = await http.get<TransactionPageV2>(resourcePath, {
			baseURL: apiVersionBase(version),
			params,
		});

		return {
			...response.data,
			content: (response.data.content ?? []) as TransactionResponseV2[],
		};
	}

	const response = await http.get<TransactionPage>(resourcePath, {
		baseURL: apiVersionBase(version),
		params,
	});

	return {
		...response.data,
		content: (response.data.content ?? []) as TransactionResponse[],
	};
}

export async function updateTransaction(
	id: number,
	request: TransactionRequest,
	scope?: TransactionApiScope
): Promise<TransactionResponse>;
export async function updateTransaction(
	id: number,
	request: TransactionRequestV2,
	scope: TransactionApiScope & { version: "v2" }
): Promise<TransactionResponseV2>;
export async function updateTransaction(
	id: number,
	request: TransactionRequest | TransactionRequestV2,
	scope?: TransactionApiScope
): Promise<TransactionResponse | TransactionResponseV2> {
	const { version, resourcePath } = resolveScope(scope);

	if (version === "v2") {
		const response = await http.put<TransactionResponseV2>(
			`${resourcePath}/${id}`,
			request,
			{ baseURL: apiVersionBase(version) }
		);
		return response.data as TransactionResponseV2;
	}

	const response = await http.put<TransactionResponse>(
		`${resourcePath}/${id}`,
		request,
		{ baseURL: apiVersionBase(version) }
	);

	return response.data as TransactionResponse;
}

export async function getTransaction(
	id: number,
	scope?: TransactionApiScope
): Promise<TransactionResponse>;
export async function getTransaction(
	id: number,
	scope: TransactionApiScope & { version: "v2" }
): Promise<TransactionResponseV2>;
export async function getTransaction(
	id: number,
	scope?: TransactionApiScope
): Promise<TransactionResponse | TransactionResponseV2> {
	const { version, resourcePath } = resolveScope(scope);

	if (version === "v2") {
		const response = await http.get<TransactionResponseV2>(
			`${resourcePath}/${id}`,
			{ baseURL: apiVersionBase(version) }
		);
		return response.data as TransactionResponseV2;
	}

	const response = await http.get<TransactionResponse>(
		`${resourcePath}/${id}`,
		{ baseURL: apiVersionBase(version) }
	);

	return response.data as TransactionResponse;
}

export async function deleteTransaction(
	id: number,
	scope?: TransactionApiScope
): Promise<HttpResult> {
	const { version, resourcePath } = resolveScope(scope);

	const response = await http.delete(`${resourcePath}/${id}`, {
		baseURL: apiVersionBase(version),
	});

	return { status: response.status, body: response.data ?? null };
}
