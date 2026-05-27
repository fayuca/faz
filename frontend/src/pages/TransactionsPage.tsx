import { useState } from "react";
import CreateTransactionForm from "../components/CreateTransactionForm";
import TransactionForm from "../components/TransactionForm";
import TransactionTable from "../components/TransactionTable";
import {
	deleteTransaction,
	updateTransaction,
} from "../api/transactions";
import { useTransactions } from "../hooks/useTransactions";
import type { TransactionResponse } from "../types/Transaction";

function TransactionsPage() {
	const {
		transactions,

		page,
		totalPages,
		setPage,

		descriptionInput,
		setDescriptionInput,

		loading,
		reload,
	} = useTransactions();

	const [editing, setEditing] = useState<TransactionResponse | null>(null);
	const [deletingId, setDeletingId] = useState<number | null>(null);

	async function handleDelete(transaction: TransactionResponse) {
		if (!window.confirm(`Delete transaction #${transaction.id}?`)) {
			return;
		}

		setDeletingId(transaction.id);

		try {
			await deleteTransaction(transaction.id);
			if (editing?.id === transaction.id) {
				setEditing(null);
			}
			reload();
		} finally {
			setDeletingId(null);
		}
	}

	return (
		<div>
			<h1>Transactions</h1>

			<input
				type="text"
				placeholder="Search description..."
				value={descriptionInput}
				onChange={e => setDescriptionInput(e.target.value)}
			/>
			&nbsp;
			<button type="button" onClick={reload}>
				⟳
			</button>

			<h2>Create</h2>
			<CreateTransactionForm onCreated={reload} />

			{editing && (
				<>
					<h2>Edit #{editing.id}</h2>
					<TransactionForm
						key={editing.id}
						initial={editing}
						submitLabel="Save"
						onCancel={() => setEditing(null)}
						onSubmit={async request => {
							await updateTransaction(editing.id, request);
							setEditing(null);
							reload();
						}}
					/>
				</>
			)}

			{loading && <p>Loading...</p>}

			<TransactionTable
				transactions={transactions}
				onEdit={setEditing}
				onDelete={handleDelete}
				deletingId={deletingId}
			/>

			<div>
				<button
					type="button"
					onClick={() => setPage(p => p - 1)}
					disabled={page === 0}
				>
					Previous
				</button>
				&nbsp;
				<span>
					Page {page + 1} of {totalPages}
				</span>
				&nbsp;
				<button
					type="button"
					onClick={() => setPage(p => p + 1)}
					disabled={page + 1 >= totalPages}
				>
					Next
				</button>
			</div>
		</div>
	);
}

export default TransactionsPage;
