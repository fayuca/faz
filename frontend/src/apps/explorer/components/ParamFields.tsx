import type { ApiParamMap, JsonSchema } from "../../../api/manifest";
import { Select, TextInput } from "../../../ui";

type Props = {
	params?: ApiParamMap;
	values: Record<string, string>;
	onChange: (name: string, value: string) => void;
	idPrefix: string;
	layout?: "stack" | "row";
};

function fieldWrapClass(name: string, layout: Props["layout"]): string | undefined {
	if (layout !== "row") {
		return undefined;
	}

	return name === "sort"
		? "explorer__param-field explorer__param-field--wide"
		: name === "page" || name === "size"
			? "explorer__param-field explorer__param-field--compact"
			: "explorer__param-field";
}

function numberMin(schema: JsonSchema): number | undefined {
	if (schema.minimum === undefined) {
		return undefined;
	}

	const value =
		typeof schema.minimum === "number"
			? schema.minimum
			: Number(schema.minimum);

	return Number.isFinite(value) ? value : undefined;
}

function fieldPlaceholder(name: string, schema: JsonSchema): string | undefined {
	if (schema.description) {
		return schema.description;
	}

	if (schema.type === "integer" && name === "page") {
		return "0";
	}

	if (schema.type === "integer" && name === "size") {
		return "20";
	}

	if (schema.type === "integer" && name === "id") {
		return "e.g. 1";
	}

	if (name === "description") {
		return "optional filter";
	}

	if (name === "currency") {
		return "filter by ISO 4217 code";
	}

	return undefined;
}

export default function ParamFields({
	params,
	values,
	onChange,
	idPrefix,
	layout = "stack",
}: Props) {
	if (!params || Object.keys(params).length === 0) {
		return null;
	}

	const fields = Object.entries(params).map(([name, schema]) => {
		const fieldId = `${idPrefix}-${name}`;
		const value = values[name] ?? "";
		const wrapClass = fieldWrapClass(name, layout);

		if (schema.enum && schema.enum.length > 0) {
			return (
				<div key={name} className={wrapClass}>
							<label className="faz-field-label" htmlFor={fieldId}>
								{name}
							</label>
							<Select
								id={fieldId}
								value={value}
								onChange={e => onChange(name, e.target.value)}
							>
								<option value="">— any —</option>
								{schema.enum.map(option => (
									<option key={option} value={option}>
										{option}
									</option>
								))}
							</Select>
						</div>
					);
				}

				if (schema.format === "date") {
					return (
						<div key={name} className={wrapClass}>
							<label className="faz-field-label" htmlFor={fieldId}>
								{name}
							</label>
							<TextInput
								id={fieldId}
								type="date"
								value={value}
								onChange={e => onChange(name, e.target.value)}
							/>
						</div>
					);
				}

				if (schema.type === "integer" || schema.type === "number") {
					const min = numberMin(schema);
					return (
						<div key={name} className={wrapClass}>
							<label className="faz-field-label" htmlFor={fieldId}>
								{name}
							</label>
							<TextInput
								id={fieldId}
								type="number"
								min={min}
								step={schema.type === "integer" ? 1 : "any"}
								placeholder={fieldPlaceholder(name, schema)}
								value={value}
								onChange={e => onChange(name, e.target.value)}
							/>
						</div>
					);
				}

				return (
					<div key={name} className={wrapClass}>
						<label className="faz-field-label" htmlFor={fieldId}>
							{name}
						</label>
						<TextInput
							id={fieldId}
							placeholder={fieldPlaceholder(name, schema)}
							value={value}
							onChange={e => onChange(name, e.target.value)}
						/>
					</div>
				);
			});

	if (layout === "row") {
		return <div className="explorer__param-row">{fields}</div>;
	}

	return <>{fields}</>;
}
