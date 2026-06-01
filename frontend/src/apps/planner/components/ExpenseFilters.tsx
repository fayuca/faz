import {
	TRANSACTION_CATEGORIES,
	TRANSACTION_CURRENCIES,
	type Currency,
	type TransactionCategory,
} from "../../../types/Transaction";
import { Select, TextInput, Button } from "../../../ui";
import type { TransactionFilters } from "../hooks/useTransactions";
import { usePlanner } from "../plannerContext";

type Props = {
	filterInput: TransactionFilters;
	onFilterChange: <K extends keyof TransactionFilters>(
		key: K,
		value: TransactionFilters[K]
	) => void;
	onApply: () => void;
	onClear: () => void;
	filterError?: string | null;
	loading?: boolean;
};

export default function ExpenseFilters({
	filterInput,
	onFilterChange,
	onApply,
	onClear,
	filterError,
	loading,
}: Props) {
	const { filtersOpen, setFiltersOpen } = usePlanner();

	return (
		<div className="planner__filters">
			<button
				type="button"
				className="planner__filter-toggle"
				onClick={() => setFiltersOpen(!filtersOpen)}
				aria-expanded={filtersOpen}
			>
				<span>Filters</span>
				<span className="planner__filter-toggle-icon" aria-hidden>
					{filtersOpen ? "▾" : "▸"}
				</span>
			</button>

			{filtersOpen && (
				<div className="planner__filters-body">
					<div className="planner__filters-row">
						<div className="planner__filters-field">
							<label className="faz-field-label" htmlFor="filter-from">
								from
							</label>
							<TextInput
								id="filter-from"
								type="date"
								value={filterInput.from}
								onChange={e => onFilterChange("from", e.target.value)}
							/>
						</div>
						<div className="planner__filters-field">
							<label className="faz-field-label" htmlFor="filter-to">
								to
							</label>
							<TextInput
								id="filter-to"
								type="date"
								value={filterInput.to}
								onChange={e => onFilterChange("to", e.target.value)}
							/>
						</div>
						<div className="planner__filters-field">
							<label className="faz-field-label" htmlFor="filter-min">
								min amount
							</label>
							<TextInput
								id="filter-min"
								type="number"
								min={0}
								step="0.01"
								placeholder="Min"
								value={filterInput.minAmount}
								onChange={e =>
									onFilterChange("minAmount", e.target.value)
								}
							/>
						</div>
						<div className="planner__filters-field">
							<label className="faz-field-label" htmlFor="filter-max">
								max amount
							</label>
							<TextInput
								id="filter-max"
								type="number"
								min={0}
								step="0.01"
								placeholder="Max"
								value={filterInput.maxAmount}
								onChange={e =>
									onFilterChange("maxAmount", e.target.value)
								}
							/>
						</div>
					</div>
					<div className="planner__filters-row">
						<div className="planner__filters-field planner__filters-field--wide">
							<label className="faz-field-label" htmlFor="filter-description">
								description
							</label>
							<TextInput
								id="filter-description"
								type="text"
								placeholder="Contains…"
								value={filterInput.description}
								onChange={e =>
									onFilterChange("description", e.target.value)
								}
							/>
						</div>
						<div className="planner__filters-field">
							<label className="faz-field-label" htmlFor="filter-currency">
								currency
							</label>
							<Select
								id="filter-currency"
								value={filterInput.currency}
								onChange={e =>
									onFilterChange(
										"currency",
										e.target.value as Currency | ""
									)
								}
							>
								<option value="">— any —</option>
								{TRANSACTION_CURRENCIES.map(currency => (
									<option key={currency} value={currency}>
										{currency}
									</option>
								))}
							</Select>
						</div>
						<div className="planner__filters-field">
							<label className="faz-field-label" htmlFor="filter-category">
								category
							</label>
							<Select
								id="filter-category"
								value={filterInput.category}
								onChange={e =>
									onFilterChange(
										"category",
										e.target.value as TransactionCategory | ""
									)
								}
							>
								<option value="">— any —</option>
								{TRANSACTION_CATEGORIES.map(category => (
									<option key={category} value={category}>
										{category}
									</option>
								))}
							</Select>
						</div>
					</div>
					<div className="planner__filters-actions">
						{filterError && (
							<p
								className="planner__filters-error faz-form-error"
								role="alert"
							>
								{filterError}
							</p>
						)}
						<div className="planner__filters-buttons">
							<Button
								type="button"
								disabled={loading}
								onClick={onClear}
							>
								Clear
							</Button>
							<Button
								type="button"
								variant="primary"
								disabled={loading}
								onClick={onApply}
							>
								Search
							</Button>
						</div>
					</div>
				</div>
			)}
		</div>
	);
}
