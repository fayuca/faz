import { useState } from "react";
import { createTransaction } from "../api/transactions";
import type { TransactionRequest } from "../types/Transaction";
import TransactionForm from "./TransactionForm";

function CreateTransactionForm({ onCreated }: { onCreated: () => void }) {
	const [formKey, setFormKey] = useState(0);

	async function onSubmit(request: TransactionRequest) {
		await createTransaction(request);
		setFormKey(k => k + 1);
		onCreated();
	}

	return (
		<TransactionForm
			key={formKey}
			submitLabel="Create"
			onSubmit={onSubmit}
		/>
	);
}

export default CreateTransactionForm;
