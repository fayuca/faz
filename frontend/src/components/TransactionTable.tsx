import type { TransactionResponse } from "../types/Transaction";

type Props = {
	transactions: TransactionResponse[];
};

function TransactionTable({ transactions }: Props) {
	return (
		<table>
			<thead>
				<tr>
					<th>ID</th>
					<th>Amount</th>
					<th>Description</th>
					<th>Category</th>
				</tr>
			</thead>

			<tbody>
				{transactions.map((transaction) => (
					<tr key={transaction.id}>
						<td>{transaction.id}</td>
						<td>{transaction.amount}</td>
						<td>{transaction.description}</td>
						<td>{transaction.category}</td>
					</tr>
				))}
			</tbody>
		</table>
	);
}

export default TransactionTable;
