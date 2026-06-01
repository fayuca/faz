import { useState } from "react";
import type { ApiParamMap } from "../../../api/manifest";
import ParamFields from "./ParamFields";

const PAGINATION_PARAMS = ["page", "size", "sort"] as const;
const PAGINATION_PARAM_NAMES = new Set<string>(PAGINATION_PARAMS);

function splitQueryParams(queryParams: ApiParamMap): {
	filters: ApiParamMap;
	pagination: ApiParamMap;
} {
	const filters: ApiParamMap = {};

	for (const [name, schema] of Object.entries(queryParams)) {
		if (!PAGINATION_PARAM_NAMES.has(name)) {
			filters[name] = schema;
		}
	}

	const pagination: ApiParamMap = {};
	for (const name of PAGINATION_PARAMS) {
		const schema = queryParams[name];
		if (schema) {
			pagination[name] = schema;
		}
	}

	return { filters, pagination };
}

type Props = {
	queryParams?: ApiParamMap;
	values: Record<string, string>;
	onChange: (name: string, value: string) => void;
};

/** Manifest-driven query fields for GET list (collapsible filters + pagination). */
export default function GetListForm({ queryParams, values, onChange }: Props) {
	const [filtersOpen, setFiltersOpen] = useState(false);

	if (!queryParams || Object.keys(queryParams).length === 0) {
		return null;
	}

	const { filters, pagination } = splitQueryParams(queryParams);
	const hasFilters = Object.keys(filters).length > 0;
	const hasPagination = Object.keys(pagination).length > 0;

	return (
		<div className="explorer__get-list-form">
			{hasFilters && (
				<div className="explorer__filters">
					<button
						type="button"
						className="explorer__filter-toggle"
						onClick={() => setFiltersOpen(current => !current)}
						aria-expanded={filtersOpen}
					>
						<span>Filters</span>
						<span className="explorer__filter-toggle-icon" aria-hidden>
							{filtersOpen ? "▾" : "▸"}
						</span>
					</button>

					{filtersOpen && (
						<div className="explorer__filters-body">
							<ParamFields
								params={filters}
								values={values}
								onChange={onChange}
								idPrefix="q"
							/>
						</div>
					)}
				</div>
			)}

			{hasPagination && (
				<ParamFields
					params={pagination}
					values={values}
					onChange={onChange}
					idPrefix="q"
					layout="row"
				/>
			)}
		</div>
	);
}
