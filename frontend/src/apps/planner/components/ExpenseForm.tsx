import { useState } from "react";
import { isAxiosError } from "axios";
import {
	createTransaction,
	updateTransaction,
} from "../../../api/transactions";
import { formatApiErrorBody } from "../../../api/formatApiError";
import {
	TRANSACTION_CATEGORIES,
	TRANSACTION_CURRENCIES,
	type Currency,
	type TransactionCategory,
	type TransactionResponseV2,
} from "../../../types/Transaction";
import { Button, Select, TextInput } from "../../../ui";
import {
	formatZodError,
	parseTransactionRequestV2,
} from "../../../validation/transactionRequest";
import { TransactionMessages } from "../../../validation/transactionMessages";
import type { ExpenseFormMode } from "../plannerContext";
import { PLANNER_API_SCOPE } from "../plannerApi";

type Props = {
	mode: ExpenseFormMode;
	transaction?: TransactionResponseV2;
	onSuccess: () => void;
};

type FormState = {
	date: string;
	amount: string;
	description: string;
	category: TransactionCategory;
	currency: Currency;
};

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

function toDateInput(iso: string): string {
	const value = new Date(iso);
	const year = value.getFullYear();
	const month = String(value.getMonth() + 1).padStart(2, "0");
	const day = String(value.getDate()).padStart(2, "0");
	return `${year}-${month}-${day}`;
}

function emptyFormState(): FormState {
	return {
		date: todayLocalDate(),
		amount: "",
		description: "",
		category: "OTHER",
		currency: "USD",
	};
}

function formFromTransaction(transaction: TransactionResponseV2): FormState {
	return {
		date: toDateInput(transaction.date),
		amount: String(transaction.amount),
		description: transaction.description,
		category: transaction.category,
		currency: transaction.currency,
	};
}

function initialFormState(
	mode: ExpenseFormMode,
	transaction?: TransactionResponseV2
): FormState {
	if (mode === "update" && transaction) {
		return formFromTransaction(transaction);
	}

	return emptyFormState();
}

export default function ExpenseForm({ mode, transaction, onSuccess }: Props) {
	const [form, setForm] = useState(() => initialFormState(mode, transaction));
	const [submitting, setSubmitting] = useState(false);
	const [error, setError] = useState<string | null>(null);

	const title = mode === "update" ? "Update expense" : "Add expense";
	const submitLabel = mode === "update" ? "Update" : "Add";

	async function handleSubmit() {
		const result = parseTransactionRequestV2({
			date: toLocalDateTime(form.date),
			amount: Number(form.amount),
			description: form.description,
			category: form.category,
			currency: form.currency,
		});

		if (!result.success) {
			setError(formatZodError(result.error));
			return;
		}

		setError(null);
		setSubmitting(true);

		try {
			if (mode === "update") {
				if (!transaction) {
					setError(TransactionMessages.requestFailed);
					return;
				}

				await updateTransaction(transaction.id, result.data, PLANNER_API_SCOPE);
			} else {
				await createTransaction(result.data, PLANNER_API_SCOPE);
			}

			onSuccess();
		} catch (e) {
			if (isAxiosError(e) && e.response?.data) {
				setError(formatApiErrorBody(e.response.data));
			} else {
				setError(TransactionMessages.requestFailed);
			}
		} finally {
			setSubmitting(false);
		}
	}

	return (
		<div className="planner__submit">
			<div className="planner__section-header">{title}</div>
			<div className="planner__submit-body">
				<div className="planner__submit-field">
					<label className="faz-field-label" htmlFor="form-date">
						date
					</label>
					<TextInput
						id="form-date"
						type="date"
						value={form.date}
						onChange={e =>
							setForm(current => ({ ...current, date: e.target.value }))
						}
					/>
				</div>
				<div className="planner__submit-field">
					<label className="faz-field-label" htmlFor="form-amount">
						amount
					</label>
					<TextInput
						id="form-amount"
						type="number"
						step="0.01"
						min={0}
						placeholder="0.00"
						value={form.amount}
						onChange={e =>
							setForm(current => ({ ...current, amount: e.target.value }))
						}
					/>
				</div>
				<div className="planner__submit-field">
					<label className="faz-field-label" htmlFor="form-currency">
						currency
					</label>
					<Select
						id="form-currency"
						value={form.currency}
						onChange={e =>
							setForm(current => ({
								...current,
								currency: e.target.value as Currency,
							}))
						}
					>
						{TRANSACTION_CURRENCIES.map(currency => (
							<option key={currency} value={currency}>
								{currency}
							</option>
						))}
					</Select>
				</div>
				<div className="planner__submit-field">
					<label className="faz-field-label" htmlFor="form-category">
						category
					</label>
					<Select
						id="form-category"
						value={form.category}
						onChange={e =>
							setForm(current => ({
								...current,
								category: e.target.value as TransactionCategory,
							}))
						}
					>
						{TRANSACTION_CATEGORIES.map(category => (
							<option key={category} value={category}>
								{category}
							</option>
						))}
					</Select>
				</div>
				<div className="planner__submit-field planner__submit-field--wide">
					<label className="faz-field-label" htmlFor="form-description">
						description
					</label>
					<TextInput
						id="form-description"
						type="text"
						placeholder="Description"
						value={form.description}
						onChange={e =>
							setForm(current => ({
								...current,
								description: e.target.value,
							}))
						}
					/>
				</div>
				<div className="planner__submit-action">
					<Button
						type="button"
						variant="primary"
						disabled={submitting}
						onClick={handleSubmit}
					>
						{submitLabel}
					</Button>
				</div>
				{error && (
					<p className="planner__submit-error faz-form-error">{error}</p>
				)}
			</div>
		</div>
	);
}
