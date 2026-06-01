import type { JsonSchema } from "../../api/manifest";

function sampleDateTime(): string {
	const now = new Date();
	const year = now.getFullYear();
	const month = String(now.getMonth() + 1).padStart(2, "0");
	const day = String(now.getDate()).padStart(2, "0");
	return `${year}-${month}-${day}T00:00:00`;
}

function sampleNumber(schema: JsonSchema): number {
	if (schema.exclusiveMinimum !== undefined) {
		const floor =
			typeof schema.exclusiveMinimum === "number"
				? schema.exclusiveMinimum
				: Number(schema.exclusiveMinimum);
		if (Number.isFinite(floor)) {
			return schema.type === "integer" ? Math.trunc(floor) + 1 : floor + 0.01;
		}
	}

	if (schema.minimum !== undefined) {
		const floor =
			typeof schema.minimum === "number"
				? schema.minimum
				: Number(schema.minimum);
		if (Number.isFinite(floor)) {
			return schema.type === "integer" ? Math.trunc(floor) : floor;
		}
	}

	return schema.type === "integer" ? 1 : 12.5;
}

function synthesizeValue(schema: JsonSchema): unknown {
	if (schema.enum && schema.enum.length > 0) {
		return schema.enum[0];
	}

	switch (schema.type) {
		case "string":
			if (schema.format === "date-time") {
				return sampleDateTime();
			}
			return schema.minLength && schema.minLength > 0 ? "string" : "";
		case "number":
		case "integer":
			return sampleNumber(schema);
		case "object": {
			const result: Record<string, unknown> = {};
			const properties = schema.properties ?? {};
			const required = schema.required ?? Object.keys(properties);

			for (const key of required) {
				const propertySchema = properties[key];
				if (propertySchema) {
					result[key] = synthesizeValue(propertySchema);
				}
			}

			return result;
		}
		case "boolean":
			return false;
		default:
			return null;
	}
}

export function synthesizePlaceholder(schema: JsonSchema): string {
	return JSON.stringify(synthesizeValue(schema), null, 2);
}
