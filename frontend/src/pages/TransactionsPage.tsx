import { useEffect, useState } from "react";
import { getTransactions } from "../api/transactions";
import type { TransactionResponse } from "../types/Transaction";
import TransactionTable from "../components/TransactionTable";

function TransactionsPage() {
	const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		getTransactions({
			page: 0,
			size: 10,
			sort: "amount,asc",
		}).then((data) => {
			setTransactions(data.content);
		}).finally(() => {
			setLoading(false);
		});
	}, []);

	if (loading) {
		return <p>Loading...</p>;
	}

	return (
		<div>
			<h1>Transactions</h1>

			<TransactionTable transactions={transactions} />
		</div>
	);
}

export default TransactionsPage;
