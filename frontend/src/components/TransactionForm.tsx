import { useState } from "react";
import {
	TRANSACTION_CATEGORIES,
	type TransactionCategory,
	type TransactionRequest,
	type TransactionResponse,
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

function dateFromResponse(iso: string): string {
	return iso.slice(0, 10);
}

function initialFromTransaction(
	transaction?: TransactionResponse
): {
	date: string;
	amount: string;
	description: string;
	category: TransactionCategory;
} {
	if (!transaction) {
		return {
			date: todayLocalDate(),
			amount: "",
			description: "",
			category: "OTHER",
		};
	}

	return {
		date: dateFromResponse(transaction.date),
		amount: String(transaction.amount),
		description: transaction.description,
		category: transaction.category,
	};
}

type Props = {
	initial?: TransactionResponse;
	submitLabel: string;
	onSubmit: (request: TransactionRequest) => Promise<void>;
	onCancel?: () => void;
};

function TransactionForm({ initial, submitLabel, onSubmit, onCancel }: Props) {
	const [date, setDate] = useState(() => initialFromTransaction(initial).date);
	const [amount, setAmount] = useState(() => initialFromTransaction(initial).amount);
	const [description, setDescription] = useState(
		() => initialFromTransaction(initial).description
	);
	const [category, setCategory] = useState<TransactionCategory>(
		() => initialFromTransaction(initial).category
	);

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

			await onSubmit(request);
		} catch {
			setError("request failed");
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
				{TRANSACTION_CATEGORIES.map(c => (
					<option key={c} value={c}>
						{c}
					</option>
				))}
			</select>

			&nbsp;

			<button type="submit" disabled={loading}>
				{submitLabel}
			</button>

			{onCancel && (
				<>
					&nbsp;
					<button type="button" onClick={onCancel} disabled={loading}>
						Cancel
					</button>
				</>
			)}

			<br />

			{error && <p>{error}</p>}
		</form>
	);
}

export default TransactionForm;
