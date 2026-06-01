export type ApiVersion = "v1" | "v2";

export const DEFAULT_API_VERSION: ApiVersion = "v1";

/** Resource path after `/api/{version}` — e.g. `/transactions`. */
export const TRANSACTIONS_PATH = "/transactions";

export function apiVersionBase(version: ApiVersion): string {
	return `/api/${version}`;
}

export function buildApiPath(
	version: ApiVersion,
	resourcePath: string
): string {
	return `${apiVersionBase(version)}${resourcePath}`;
}
