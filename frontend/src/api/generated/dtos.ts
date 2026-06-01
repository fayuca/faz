import type { components } from "./api";

type Schemas = components["schemas"];

/** HTTP error body from {@link Schemas.ApiError}. */
export type ApiError = Schemas["ApiError"];

export type TransactionRequest = Schemas["TransactionRequest"];

/** Successful transaction payloads always include core fields. */
export type TransactionResponse = Required<
	Pick<
		Schemas["TransactionResponse"],
		"id" | "date" | "amount" | "description" | "category"
	>
>;

export type TransactionCriteria = Schemas["TransactionCriteria"];

export type TransactionPage = Omit<
	Schemas["PageResponseTransactionResponse"],
	"content"
> & {
	content: TransactionResponse[];
};

export type TransactionCategory = NonNullable<
	TransactionResponse["category"]
>;

export type Currency = Schemas["TransactionRequestV2"]["currency"];

export type TransactionRequestV2 = Schemas["TransactionRequestV2"];

export type TransactionResponseV2 = Required<
	Pick<
		Schemas["TransactionResponseV2"],
		"id" | "date" | "amount" | "description" | "category" | "currency"
	>
>;

export type TransactionPageV2 = Omit<
	Schemas["PageResponseTransactionResponseV2"],
	"content"
> & {
	content: TransactionResponseV2[];
};

/** Criteria plus Spring pagination / sort query params (flattened for axios). */
export type TransactionListParams = TransactionCriteria & {
	page?: number;
	size?: number;
	sort?: string;
};

export type ApiManifestDto = Schemas["ApiManifest"];
export type ApiResourceDto = Schemas["ApiResource"];
export type ApiOperationDto = Schemas["ApiOperation"];
export type ApiRequestBodyDto = Schemas["ApiRequestBody"];
