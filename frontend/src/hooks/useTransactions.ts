import { useEffect, useState } from "react";
import { getTransactions } from "../api/transactions";
import type { TransactionResponse } from "../types/Transaction";

export function useTransactions() {
	const [loading, setLoading] = useState(false);
	const [reloadVersion, setReloadVersion] = useState(0);

	const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
	const [page, setPage] = useState(0);
	const [totalPages, setTotalPages] = useState(0);

	const [descriptionInput, setDescriptionInput] = useState("");
	const [description, setDescription] = useState("");

	useEffect(() => {
		async function load() {
			setLoading(true);

			try {
				const data = await getTransactions({
					page,
					size: 10,
					sort: "amount,asc",
					description,
				});

				setTransactions(data.content);
				setTotalPages(data.totalPages);
			} finally {
				setLoading(false);
			}
		}

		load();
	}, [page, description, reloadVersion]);

	useEffect(() => {
		const timeout = setTimeout(() => {
			setPage(0);
			setDescription(descriptionInput);
		}, 500);

		return () => clearTimeout(timeout);
	}, [descriptionInput]);

	function reload() {
		setReloadVersion(r => r + 1);
	}

	return {
		transactions,

		page,
		totalPages,
		setPage,

		descriptionInput,
		setDescriptionInput,

		loading,
		reload,
	};
}
