import type { TransactionResponseV2 } from "../../../types/Transaction";
import { formatUsdAmount } from "../../../formatAmount";
import { Button } from "../../../ui";
import type {
	SortField,
	TransactionSort,
} from "../hooks/useTransactions";

type Props = {
	transactions: TransactionResponseV2[];
	loading: boolean;
	deletingId: number | null;
	sort: TransactionSort;
	onSort: (field: SortField) => void;
	onUpdate: (transaction: TransactionResponseV2) => void;
	onDelete: (transaction: TransactionResponseV2) => void;
};

type SortableColumn = {
	field: SortField;
	label: string;
	align: "start" | "center" | "end";
};

const SORTABLE_COLUMNS: SortableColumn[] = [
	{ field: "date", label: "Date", align: "start" },
	{ field: "description", label: "Description", align: "start" },
	{ field: "amount", label: "Amount", align: "end" },
	{ field: "currency", label: "Currency", align: "center" },
	{ field: "category", label: "Category", align: "start" },
];

function formatDate(iso: string): string {
	return new Date(iso).toLocaleDateString();
}

function columnAlignClass(align: SortableColumn["align"]): string {
	return `planner__col-align-${align}`;
}

function sortIndicator(
	field: SortField,
	sort: TransactionSort
): string | null {
	if (sort.field !== field) {
		return null;
	}

	return sort.direction === "asc" ? "▲" : "▼";
}

function ariaSortValue(
	field: SortField,
	sort: TransactionSort
): "ascending" | "descending" | "none" {
	if (sort.field !== field) {
		return "none";
	}

	return sort.direction === "asc" ? "ascending" : "descending";
}

export default function ExpensesTable({
	transactions,
	loading,
	deletingId,
	sort,
	onSort,
	onUpdate,
	onDelete,
}: Props) {
	return (
		<div className="planner__table-wrap">
			<table className="planner__table">
				<colgroup>
					<col className="planner__col-date" />
					<col className="planner__col-description" />
					<col className="planner__col-amount" />
					<col className="planner__col-currency" />
					<col className="planner__col-category" />
					<col className="planner__col-actions" />
				</colgroup>
				<thead>
					<tr>
						{SORTABLE_COLUMNS.map(column => {
							const indicator = sortIndicator(column.field, sort);

							return (
							<th
								key={column.field}
								scope="col"
								className={columnAlignClass(column.align)}
							>
								<button
									type="button"
									className="planner__sort-header"
									aria-sort={ariaSortValue(column.field, sort)}
									onClick={() => onSort(column.field)}
								>
									<span>{column.label}</span>
									{indicator && (
										<span
											className="planner__sort-indicator"
											aria-hidden
										>
											{indicator}
										</span>
									)}
								</button>
							</th>
							);
						})}
						<th scope="col" className="planner__col-align-end" aria-label="Actions" />
					</tr>
				</thead>
				<tbody>
					{loading && transactions.length === 0 && (
						<tr>
							<td colSpan={6} className="planner__table-empty">
								Loading…
							</td>
						</tr>
					)}
					{!loading && transactions.length === 0 && (
						<tr>
							<td colSpan={6} className="planner__table-empty">
								No transactions match these filters.
							</td>
						</tr>
					)}
					{transactions.map(transaction => (
						<tr key={transaction.id}>
							<td className="planner__col-align-start">
								{formatDate(transaction.date)}
							</td>
							<td className="planner__cell-description planner__col-align-start">
								{transaction.description}
							</td>
							<td className="planner__cell-amount planner__col-align-end">
								{formatUsdAmount(transaction.amount)}
							</td>
							<td className="planner__col-align-center">
								{transaction.currency}
							</td>
							<td className="planner__col-align-start">
								{transaction.category}
							</td>
							<td className="planner__cell-actions planner__col-align-end">
								<Button
									type="button"
									onClick={() => onUpdate(transaction)}
								>
									Update
								</Button>
								<Button
									type="button"
									variant="danger"
									disabled={deletingId === transaction.id}
									onClick={() => onDelete(transaction)}
								>
									{deletingId === transaction.id ? "…" : "Delete"}
								</Button>
							</td>
						</tr>
					))}
				</tbody>
			</table>
		</div>
	);
}
