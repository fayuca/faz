import type { ApiParamMap, JsonSchema } from "../../api/manifest";

export type ManifestQueryParams = Record<string, string | number | undefined>;

export function emptyParamValues(params?: ApiParamMap): Record<string, string> {
	if (!params) {
		return {};
	}

	return Object.fromEntries(Object.keys(params).map(name => [name, ""]));
}

export function optionalNumber(value: string): number | undefined {
	if (value === "") {
		return undefined;
	}

	const n = Number(value);
	return Number.isFinite(n) ? n : undefined;
}

export function optionalString(value: string): string | undefined {
	return value === "" ? undefined : value;
}

function valueFromSchema(
	raw: string,
	schema: JsonSchema
): string | number | undefined {
	if (raw === "") {
		return undefined;
	}

	switch (schema.type) {
		case "integer":
		case "number":
			return optionalNumber(raw);
		case "string":
			if (schema.enum && schema.enum.length > 0) {
				return schema.enum.includes(raw) ? raw : undefined;
			}
			return raw;
		default:
			return optionalString(raw);
	}
}

/** Build axios query params from manifest field schemas and form values. */
export function paramsFromManifestValues(
	params: ApiParamMap | undefined,
	values: Record<string, string>
): ManifestQueryParams {
	if (!params) {
		return {};
	}

	const result: ManifestQueryParams = {};

	for (const [name, schema] of Object.entries(params)) {
		const value = valueFromSchema(values[name] ?? "", schema);
		if (value !== undefined) {
			result[name] = value;
		}
	}

	return result;
}
