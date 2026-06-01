import type { JsonSchema } from "../../api/manifest";
import { ExplorerMessages } from "./explorerMessages";

const LOCAL_DATE_TIME_PATTERN =
	/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/;

export type ValidatedBody = Record<string, unknown>;

export type BodyValidationResult =
	| { ok: true; value: ValidatedBody }
	| { ok: false; message: string };

export type PathParamResult =
	| { ok: true; value: number }
	| { ok: false; message: string };

function isValidLocalDateTime(value: string): boolean {
	const match = value.match(
		/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})$/
	);
	if (!match) {
		return false;
	}

	const year = Number(match[1]);
	const month = Number(match[2]);
	const day = Number(match[3]);
	const hour = Number(match[4]);
	const minute = Number(match[5]);
	const second = Number(match[6]);

	if (month < 1 || month > 12 || hour > 23 || minute > 59 || second > 59) {
		return false;
	}

	const parsed = new Date(year, month - 1, day);
	return (
		parsed.getFullYear() === year &&
		parsed.getMonth() === month - 1 &&
		parsed.getDate() === day
	);
}

function numericBound(value: number | string | boolean | undefined): number | undefined {
	if (value === undefined || typeof value === "boolean") {
		return undefined;
	}

	const n = typeof value === "number" ? value : Number(value);
	return Number.isFinite(n) ? n : undefined;
}

function validateValue(
	value: unknown,
	schema: JsonSchema,
	fieldName: string
): string | undefined {
	if (schema.enum && schema.enum.length > 0) {
		if (typeof value !== "string" || !schema.enum.includes(value)) {
			return ExplorerMessages.paramInvalid(fieldName);
		}
		return undefined;
	}

	switch (schema.type) {
		case "string": {
			if (typeof value !== "string") {
				return ExplorerMessages.paramInvalid(fieldName);
			}

			if (schema.format === "date-time") {
				if (!LOCAL_DATE_TIME_PATTERN.test(value) || !isValidLocalDateTime(value)) {
					return "Enter a valid date and time (for example 2026-05-29T12:00:00).";
				}
			}

			if (schema.minLength !== undefined && value.trim().length < schema.minLength) {
				return ExplorerMessages.paramRequired(fieldName);
			}

			return undefined;
		}
		case "integer": {
			if (typeof value !== "number" || !Number.isInteger(value)) {
				return ExplorerMessages.paramInvalid(fieldName);
			}

			const min = numericBound(schema.minimum);
			if (min !== undefined && value < min) {
				return ExplorerMessages.paramInvalid(fieldName);
			}

			return undefined;
		}
		case "number": {
			if (typeof value !== "number" || !Number.isFinite(value)) {
				return ExplorerMessages.paramInvalid(fieldName);
			}

			const exclusiveMin = numericBound(schema.exclusiveMinimum);
			if (exclusiveMin !== undefined && value <= exclusiveMin) {
				return ExplorerMessages.paramInvalid(fieldName);
			}

			const min = numericBound(schema.minimum);
			if (min !== undefined && value < min) {
				return ExplorerMessages.paramInvalid(fieldName);
			}

			return undefined;
		}
		default:
			return undefined;
	}
}

export function validateRequestBody(
	input: unknown,
	schema?: JsonSchema
): BodyValidationResult {
	if (!schema) {
		if (typeof input === "object" && input !== null && !Array.isArray(input)) {
			return { ok: true, value: input as ValidatedBody };
		}

		return { ok: false, message: ExplorerMessages.validationSummary };
	}

	if (typeof input !== "object" || input === null || Array.isArray(input)) {
		return { ok: false, message: ExplorerMessages.validationSummary };
	}

	const body = input as Record<string, unknown>;
	const required = schema.required ?? [];
	const properties = schema.properties ?? {};

	for (const field of required) {
		const value = body[field];
		if (value === null || value === undefined) {
			return { ok: false, message: ExplorerMessages.validationSummary };
		}

		if (typeof value === "string" && properties[field]?.minLength && value.trim().length === 0) {
			return { ok: false, message: ExplorerMessages.validationSummary };
		}
	}

	for (const [field, propertySchema] of Object.entries(properties)) {
		if (!(field in body)) {
			continue;
		}

		const error = validateValue(body[field], propertySchema, field);
		if (error) {
			return { ok: false, message: error };
		}
	}

	return { ok: true, value: body };
}

export function parsePathParam(
	raw: string,
	schema: JsonSchema | undefined,
	paramName: string
): PathParamResult {
	const trimmed = raw.trim();
	if (trimmed === "") {
		return { ok: false, message: ExplorerMessages.paramRequired(paramName) };
	}

	if (schema?.type === "integer") {
		const value = Number(trimmed);
		const min = numericBound(schema.minimum) ?? 1;

		if (!Number.isInteger(value) || value < min) {
			return { ok: false, message: ExplorerMessages.paramInvalid(paramName) };
		}

		return { ok: true, value };
	}

	return { ok: false, message: ExplorerMessages.paramInvalid(paramName) };
}
