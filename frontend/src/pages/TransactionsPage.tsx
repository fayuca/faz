import CreateTransactionForm from "../components/CreateTransactionForm";
import TransactionTable from "../components/TransactionTable";
import { useTransactions } from "../hooks/useTransactions";

function TransactionsPage() {
	const {
		transactions,

		page,
		totalPages,
		setPage,

		descriptionInput,
		setDescriptionInput,

		loading,
		reload
	} = useTransactions();

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
			<button onClick={reload}>
				⟳
			</button>

			<CreateTransactionForm onCreated={reload} />

			{loading && <p>Loading...</p>}

			<TransactionTable transactions={transactions} />

			<div>
				<button
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
