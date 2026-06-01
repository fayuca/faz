import { useState } from "react";
import { deleteTransaction } from "../../../api/transactions";
import type { TransactionResponseV2 } from "../../../types/Transaction";
import { Button } from "../../../ui";
import ExpenseFilters from "../components/ExpenseFilters";
import ExpenseForm from "../components/ExpenseForm";
import ExpensesTable from "../components/ExpensesTable";
import PlannerPageHead from "../components/PlannerPageHead";
import { useTransactions } from "../hooks/useTransactions";
import { usePlanner } from "../plannerContext";
import { PLANNER_API_SCOPE } from "../plannerApi";

function TransactionsView() {
	const {
		view,
		formMode,
		editingTransaction,
		showExpenses,
		showUpdateExpense,
	} = usePlanner();

	const {
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
	} = useTransactions();

	const [deletingId, setDeletingId] = useState<number | null>(null);

	async function handleDelete(transaction: TransactionResponseV2) {
		setDeletingId(transaction.id);

		try {
			await deleteTransaction(transaction.id, PLANNER_API_SCOPE);
			reload();
		} finally {
			setDeletingId(null);
		}
	}

	function handleFormSuccess() {
		reload();
		showExpenses();
	}

	const pageTitle =
		view === "form"
			? formMode === "update"
				? "Update expense"
				: "Add expense"
			: "Expenses";

	return (
		<div className="planner">
			<PlannerPageHead
				title={pageTitle}
				subtitle="Track personal spending lines."
			/>

			{view === "form" ? (
				<ExpenseForm
					key={
						formMode === "update" && editingTransaction
							? `update-${editingTransaction.id}`
							: "create"
					}
					mode={formMode}
					transaction={editingTransaction ?? undefined}
					onSuccess={handleFormSuccess}
				/>
			) : (
				<div className="planner__layout">
					<ExpenseFilters
						filterInput={filterInput}
						onFilterChange={setFilterField}
						onApply={applyFilters}
						onClear={clearFilters}
						filterError={filterError}
						loading={loading}
					/>

					<ExpensesTable
						transactions={transactions}
						loading={loading}
						deletingId={deletingId}
						sort={sort}
						onSort={toggleSort}
						onUpdate={showUpdateExpense}
						onDelete={handleDelete}
					/>

					<div className="planner__pagination">
						<Button
							type="button"
							onClick={() => setPage(p => p - 1)}
							disabled={page === 0 || loading}
						>
							Previous
						</Button>
						<span>
							Page {page + 1} of {totalPages || 1}
						</span>
						<Button
							type="button"
							onClick={() => setPage(p => p + 1)}
							disabled={page + 1 >= totalPages || loading}
						>
							Next
						</Button>
					</div>
				</div>
			)}
		</div>
	);
}

export default TransactionsView;
