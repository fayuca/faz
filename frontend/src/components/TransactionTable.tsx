import type { TransactionResponse } from "../types/Transaction";

type Props = {
	transactions: TransactionResponse[];
	onEdit: (transaction: TransactionResponse) => void;
	onDelete: (transaction: TransactionResponse) => void;
	deletingId: number | null;
};

function formatDate(iso: string): string {
	return new Date(iso).toLocaleDateString();
}

function TransactionTable({
	transactions,
	onEdit,
	onDelete,
	deletingId,
}: Props) {
	return (
		<table>
			<thead>
				<tr>
					<th>ID</th>
					<th>Date</th>
					<th>Amount</th>
					<th>Description</th>
					<th>Category</th>
					<th>Actions</th>
				</tr>
			</thead>

			<tbody>
				{transactions.map(transaction => (
					<tr key={transaction.id}>
						<td>{transaction.id}</td>
						<td>{formatDate(transaction.date)}</td>
						<td>{transaction.amount}</td>
						<td>{transaction.description}</td>
						<td>{transaction.category}</td>
						<td>
							<button
								type="button"
								onClick={() => onEdit(transaction)}
							>
								Edit
							</button>
							&nbsp;
							<button
								type="button"
								disabled={deletingId === transaction.id}
								onClick={() => onDelete(transaction)}
							>
								{deletingId === transaction.id ? "…" : "Delete"}
							</button>
						</td>
					</tr>
				))}
			</tbody>
		</table>
	);
}

export default TransactionTable;
