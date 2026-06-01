import { isAxiosError } from "axios";
import { useState } from "react";
import type { ApiVersion } from "../../../api/paths";
import {
	createTransaction,
	deleteTransaction,
	getTransaction,
	getTransactions,
	type HttpResult,
	type TransactionListParams,
	type TransactionRequest,
	type TransactionRequestV2,
	updateTransaction,
} from "../../../api/transactions";
import type { ManifestQueryParams } from "../paramValues";
import { ExplorerMessages } from "../explorerMessages";
import type { ValidatedBody } from "../validateManifestSchema";
import type { ExplorerResponse } from "../types";

function isHttpResult(value: unknown): value is HttpResult {
	return (
		typeof value === "object" &&
		value !== null &&
		"status" in value &&
		typeof (value as HttpResult).status === "number"
	);
}

function toExplorerSuccess(data: unknown): ExplorerResponse {
	if (isHttpResult(data)) {
		return { status: data.status, body: data.body };
	}

	return { status: 200, body: data };
}

type Options = {
	version: ApiVersion;
	resourcePath: string;
};

export function useExplorerRequest({ version, resourcePath }: Options) {
	const [response, setResponse] = useState<ExplorerResponse | null>(null);
	const [clientError, setClientError] = useState<string | null>(null);
	const [loading, setLoading] = useState(false);

	const scope = { version, resourcePath };

	function reportClientError(message: string) {
		setClientError(message);
		setResponse(null);
	}

	async function runRequest(request: () => Promise<unknown>) {
		setClientError(null);
		setResponse(null);
		setLoading(true);

		try {
			const data = await request();
			setResponse(toExplorerSuccess(data));
		} catch (e) {
			if (isAxiosError(e) && e.response) {
				setResponse({
					status: e.response.status,
					body: e.response.data,
				});
			} else {
				setClientError(ExplorerMessages.requestFailed);
			}
		} finally {
			setLoading(false);
		}
	}

	function sendListRequest(params: ManifestQueryParams) {
		return runRequest(() =>
			getTransactions(params as TransactionListParams, scope)
		);
	}

	function sendGetByIdRequest(id: number) {
		return runRequest(() => getTransaction(id, scope));
	}

	function writeBody(request: ValidatedBody) {
		if (version === "v2") {
			return request as TransactionRequestV2;
		}
		return request as TransactionRequest;
	}

	function sendCreateRequest(request: ValidatedBody) {
		return runRequest(() => createTransaction(writeBody(request), scope));
	}

	function sendUpdateRequest(id: number, request: ValidatedBody) {
		return runRequest(() =>
			updateTransaction(id, writeBody(request), scope)
		);
	}

	function sendDeleteRequest(id: number) {
		return runRequest(() => deleteTransaction(id, scope));
	}

	return {
		response,
		clientError,
		loading,
		sendListRequest,
		sendGetByIdRequest,
		sendCreateRequest,
		sendUpdateRequest,
		sendDeleteRequest,
		reportClientError,
	};
}
