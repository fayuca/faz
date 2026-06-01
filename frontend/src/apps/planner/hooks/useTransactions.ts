import { useEffect, useState } from "react";
import { getTransactions } from "../../../api/transactions";
import type {
	Currency,
	TransactionCategory,
	TransactionResponseV2,
} from "../../../types/Transaction";
import { TransactionMessages } from "../../../validation/transactionMessages";
import { PLANNER_API_SCOPE } from "../plannerApi";

export type TransactionFilters = {
	description: string;
	from: string;
	to: string;
	category: TransactionCategory | "";
	currency: Currency | "";
	minAmount: string;
	maxAmount: string;
};

const EMPTY_FILTERS: TransactionFilters = {
	description: "",
	from: "",
	to: "",
	category: "",
	currency: "",
	minAmount: "",
	maxAmount: "",
};

export type SortField =
	| "date"
	| "amount"
	| "currency"
	| "description"
	| "category";

export type SortDirection = "asc" | "desc";

export type TransactionSort = {
	field: SortField;
	direction: SortDirection;
};

const DEFAULT_SORT: TransactionSort = { field: "date", direction: "desc" };

const DEFAULT_DIRECTION: Record<SortField, SortDirection> = {
	date: "desc",
	amount: "asc",
	currency: "asc",
	description: "asc",
	category: "asc",
};

function parseOptionalAmount(raw: string): number | undefined {
	if (raw.trim() === "") {
		return undefined;
	}

	const value = Number(raw);
	return Number.isFinite(value) ? value : undefined;
}

function validateFilters(input: TransactionFilters): string | null {
	if (input.from && input.to && input.from > input.to) {
		return TransactionMessages.filterDateRange;
	}

	const minAmount = parseOptionalAmount(input.minAmount);
	const maxAmount = parseOptionalAmount(input.maxAmount);

	if (
		minAmount !== undefined &&
		maxAmount !== undefined &&
		minAmount > maxAmount
	) {
		return TransactionMessages.filterAmountRange;
	}

	return null;
}

export function useTransactions() {
	const [loading, setLoading] = useState(false);
	const [reloadVersion, setReloadVersion] = useState(0);

	const [transactions, setTransactions] = useState<TransactionResponseV2[]>([]);
	const [page, setPage] = useState(0);
	const [totalPages, setTotalPages] = useState(0);

	const [filterInput, setFilterInput] = useState<TransactionFilters>(EMPTY_FILTERS);
	const [filters, setFilters] = useState<TransactionFilters>(EMPTY_FILTERS);
	const [filterError, setFilterError] = useState<string | null>(null);
	const [sort, setSort] = useState<TransactionSort>(DEFAULT_SORT);

	useEffect(() => {
		async function load() {
			setLoading(true);

			try {
				const data = await getTransactions(
					{
						page,
						size: 10,
						sort: `${sort.field},${sort.direction}`,
						description: filters.description || undefined,
						from: filters.from || undefined,
						to: filters.to || undefined,
						category: filters.category || undefined,
						currency: filters.currency || undefined,
						minAmount: parseOptionalAmount(filters.minAmount),
						maxAmount: parseOptionalAmount(filters.maxAmount),
					},
					PLANNER_API_SCOPE
				);

				setTransactions((data.content ?? []) as TransactionResponseV2[]);
				setTotalPages(data.totalPages ?? 0);
			} finally {
				setLoading(false);
			}
		}

		load();
	}, [page, filters, sort, reloadVersion]);

	function toggleSort(field: SortField) {
		setPage(0);
		setSort(current =>
			current.field === field
				? {
						field,
						direction: current.direction === "asc" ? "desc" : "asc",
					}
				: { field, direction: DEFAULT_DIRECTION[field] }
		);
	}

	function applyFilters() {
		const error = validateFilters(filterInput);

		if (error) {
			setFilterError(error);
			return;
		}

		setFilterError(null);
		setPage(0);
		setFilters(filterInput);
	}

	function clearFilters() {
		setFilterInput(EMPTY_FILTERS);
		setFilterError(null);
		setPage(0);
		setFilters(EMPTY_FILTERS);
		setSort(DEFAULT_SORT);
	}

	function reload() {
		setReloadVersion(r => r + 1);
	}

	function setFilterField<K extends keyof TransactionFilters>(
		key: K,
		value: TransactionFilters[K]
	) {
		setFilterError(null);
		setFilterInput(current => ({ ...current, [key]: value }));
	}

	return {
		transactions,
		page,
		totalPages,
		setPage,
		filterInput,
		setFilterField,
		filterError,
		applyFilters,
		clearFilters,
		sort,
		toggleSort,
		loading,
		reload,
	};
}
