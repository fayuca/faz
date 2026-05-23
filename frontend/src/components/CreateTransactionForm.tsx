import { useState } from "react";
import { createTransaction } from "../api/transactions";
import {
	TRANSACTION_CATEGORIES,
	type TransactionCategory,
	type TransactionRequest,
} from "../types/Transaction";

function CreateTransactionForm(
	{ onCreated }
		: {
			onCreated: () => void;
		}) {
	const [amount, setAmount] = useState("");
	const [description, setDescription] = useState("");
	const [category, setCategory] = useState<TransactionCategory>("OTHER");

	const [loading, setLoading] = useState(false);
	const [error, setError] = useState<string | null>(null);

	async function submit(e: React.SubmitEvent<HTMLFormElement>) {
		e.preventDefault();

		if (!amount.trim()) {
			setError("missing amount");
			return;
		}
		if (Number(amount) <= 0) {
			setError("amount is not positive");
			return;
		}

		if (!description.trim()) {
			setError("missing description");
			return;
		}

		if (!TRANSACTION_CATEGORIES.includes(category)) {
			setError("invalid category");
			return;
		}

		setError(null);
		setLoading(true);

		try {
			const request: TransactionRequest = {
				amount: Number(amount),
				description: description.trim(),
				category,
			};

			await createTransaction(request);

			setAmount("");
			setDescription("");
			setCategory("OTHER");

			onCreated();
		} catch (err) {
			setError("failed to create transaction");
		} finally {
			setLoading(false);
		}
	}

	return (
		<form onSubmit={submit}>
			<input
				type="number"
				step="0.01"
				placeholder="Amount"
				value={amount}
				onChange={e => setAmount(e.target.value)}
			/>

			&nbsp;

			<input
				type="text"
				placeholder="Description"
				value={description}
				onChange={e => setDescription(e.target.value)}
			/>

			&nbsp;

			<select
				value={category}
				onChange={e =>
					setCategory(e.target.value as TransactionCategory)
				}
			>
				{TRANSACTION_CATEGORIES.map((category) => (
					<option key={category} value={category}>
						{category}
					</option>
				))}
			</select>

			&nbsp;

			<button disabled={loading}>
				Create
			</button>

			<br />

			{error && <p>{error}</p>}
		</form>
	);
}

export default CreateTransactionForm;
