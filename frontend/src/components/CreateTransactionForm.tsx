import { useState } from "react";
import { createTransaction } from "../api/transactions";
import {
	TRANSACTION_CATEGORIES,
	type TransactionCategory,
	type TransactionRequest,
} from "../types/Transaction";

function todayLocalDate(): string {
	const now = new Date();
	const year = now.getFullYear();
	const month = String(now.getMonth() + 1).padStart(2, "0");
	const day = String(now.getDate()).padStart(2, "0");
	return `${year}-${month}-${day}`;
}

function toLocalDateTime(date: string): string {
	return `${date}T00:00:00`;
}

function CreateTransactionForm(
	{ onCreated }
		: {
			onCreated: () => void;
		}) {
	const [date, setDate] = useState(todayLocalDate);
	const [amount, setAmount] = useState("");
	const [description, setDescription] = useState("");
	const [category, setCategory] = useState<TransactionCategory>("OTHER");

	const [loading, setLoading] = useState(false);
	const [error, setError] = useState<string | null>(null);

	async function submit(e: React.SubmitEvent<HTMLFormElement>) {
		e.preventDefault();

		if (!date.trim()) {
			setError("missing date");
			return;
		}

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
				date: toLocalDateTime(date),
				amount: Number(amount),
				description: description.trim(),
				category,
			};

			await createTransaction(request);

			setDate(todayLocalDate());
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
				type="date"
				value={date}
				onChange={e => setDate(e.target.value)}
			/>

			&nbsp;

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
