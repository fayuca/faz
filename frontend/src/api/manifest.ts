import type { ApiVersion } from "./paths";
import { http } from "./http";
import type { HttpMethod } from "../ui";
import type {
	ApiManifestDto,
	ApiOperationDto,
	ApiRequestBodyDto,
	ApiResourceDto,
} from "./generated/dtos";

/** JSON Schema fragment embedded in manifest param maps and request bodies. */
export type JsonSchema = {
	type?: string;
	format?: string;
	description?: string;
	enum?: string[];
	properties?: Record<string, JsonSchema>;
	required?: string[];
	minLength?: number;
	minimum?: number | string;
	exclusiveMinimum?: number | string | boolean;
};

export type ApiParamMap = Record<string, JsonSchema>;

export type ApiRequestBody = {
	contentType: string;
	schema: JsonSchema;
};

export type ApiOperation = {
	verb: HttpMethod;
	versions: ApiVersion[];
	queryParams?: Partial<Record<ApiVersion, ApiParamMap>>;
	pathParams?: ApiParamMap;
	requestBody?: Partial<Record<ApiVersion, ApiRequestBody>>;
};

export type ApiResource = {
	id: string;
	label: string;
	path: string;
	operations: ApiOperation[];
};

export type ApiManifest = {
	contractVersion: string;
	resources: ApiResource[];
};

const DEFAULT_VERSIONS: readonly ApiVersion[] = ["v1"];

const HTTP_METHODS: readonly HttpMethod[] = ["GET", "POST", "PUT", "DELETE"];

function isHttpMethod(value: string): value is HttpMethod {
	return (HTTP_METHODS as readonly string[]).includes(value);
}

function asParamMap(value: unknown): ApiParamMap | undefined {
	if (!value || typeof value !== "object" || Array.isArray(value)) {
		return undefined;
	}

	return value as ApiParamMap;
}

function asJsonSchema(value: unknown): JsonSchema {
	if (!value || typeof value !== "object" || Array.isArray(value)) {
		return {};
	}

	return value as JsonSchema;
}

function normalizeRequestBody(body: ApiRequestBodyDto): ApiRequestBody {
	return {
		contentType: body.contentType ?? "application/json",
		schema: asJsonSchema(body.schema),
	};
}

function normalizeRequestBodies(
	bodies: ApiOperationDto["requestBody"]
): ApiOperation["requestBody"] | undefined {
	if (!bodies) {
		return undefined;
	}

	const normalized: Partial<Record<ApiVersion, ApiRequestBody>> = {};

	for (const [version, body] of Object.entries(bodies)) {
		if (body) {
			normalized[version as ApiVersion] = normalizeRequestBody(body);
		}
	}

	return Object.keys(normalized).length > 0 ? normalized : undefined;
}

const VERSION_PARAM_KEY = /^v\d+$/;

function isVersionKeyedQueryParams(params: Record<string, unknown>): boolean {
	return Object.keys(params).some(key => VERSION_PARAM_KEY.test(key));
}

function normalizeQueryParams(
	params: ApiOperationDto["queryParams"]
): ApiOperation["queryParams"] | undefined {
	if (!params) {
		return undefined;
	}

	const record = params as Record<string, unknown>;

	// Legacy flat manifest (field names at top level) — use the same map for each
	// version until the backend serves queryParams.v1 / queryParams.v2 slices.
	if (!isVersionKeyedQueryParams(record)) {
		const flat = asParamMap(params);
		return flat ? { v1: flat, v2: flat } : undefined;
	}

	const normalized: Partial<Record<ApiVersion, ApiParamMap>> = {};

	for (const [version, map] of Object.entries(record)) {
		const paramMap = asParamMap(map);
		if (paramMap) {
			normalized[version as ApiVersion] = paramMap;
		}
	}

	return Object.keys(normalized).length > 0 ? normalized : undefined;
}

function normalizeOperation(operation: ApiOperationDto): ApiOperation {
	const verb = operation.verb ?? "GET";

	return {
		verb: isHttpMethod(verb) ? verb : "GET",
		versions: (operation.versions ?? [...DEFAULT_VERSIONS]) as ApiVersion[],
		queryParams: normalizeQueryParams(operation.queryParams),
		pathParams: asParamMap(operation.pathParams),
		requestBody: normalizeRequestBodies(operation.requestBody),
	};
}

function normalizeResource(resource: ApiResourceDto): ApiResource {
	return {
		id: resource.id ?? "",
		label: resource.label ?? "",
		path: resource.path ?? "",
		operations: (resource.operations ?? []).map(normalizeOperation),
	};
}

function normalizeManifest(raw: ApiManifestDto): ApiManifest {
	return {
		contractVersion: raw.contractVersion ?? "v1",
		resources: (raw.resources ?? []).map(normalizeResource),
	};
}

export async function fetchApiManifest(): Promise<ApiManifest> {
	const response = await http.get<ApiManifestDto>("/manifest");
	return normalizeManifest(response.data);
}

export function resourceById(
	manifest: ApiManifest,
	id: string
): ApiResource | undefined {
	return manifest.resources.find(r => r.id === id);
}

export function verbsForResource(resource: ApiResource): HttpMethod[] {
	return resource.operations.map(o => o.verb);
}

export function versionsForVerb(
	resource: ApiResource,
	verb: HttpMethod
): readonly ApiVersion[] {
	return (
		resource.operations.find(o => o.verb === verb)?.versions ??
		DEFAULT_VERSIONS
	);
}

export function operationForVerb(
	resource: ApiResource,
	verb: HttpMethod
): ApiOperation | undefined {
	return resource.operations.find(o => o.verb === verb);
}

export function requestBodySchemaForVersion(
	operation: ApiOperation | undefined,
	version: ApiVersion
): JsonSchema | undefined {
	return operation?.requestBody?.[version]?.schema;
}

export function queryParamsForVersion(
	operation: ApiOperation | undefined,
	version: ApiVersion
): ApiParamMap | undefined {
	return operation?.queryParams?.[version];
}

export function clampApiVersion(
	versions: readonly ApiVersion[],
	current: ApiVersion
): ApiVersion {
	return versions.includes(current) ? current : versions[0];
}
