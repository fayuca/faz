import {
	createContext,
	useCallback,
	useContext,
	useMemo,
	useState,
	type ReactNode,
} from "react";
import type { TransactionResponseV2 } from "../../types/Transaction";

export type PlannerView = "expenses" | "form";
export type ExpenseFormMode = "create" | "update";

type PlannerContextValue = {
	view: PlannerView;
	formMode: ExpenseFormMode;
	editingTransaction: TransactionResponseV2 | null;
	filtersOpen: boolean;
	setFiltersOpen: (open: boolean) => void;
	showExpenses: () => void;
	showAddExpense: () => void;
	showUpdateExpense: (transaction: TransactionResponseV2) => void;
};

const PlannerContext = createContext<PlannerContextValue | null>(null);

export function PlannerProvider({ children }: { children: ReactNode }) {
	const [view, setView] = useState<PlannerView>("expenses");
	const [formMode, setFormMode] = useState<ExpenseFormMode>("create");
	const [editingTransaction, setEditingTransaction] =
		useState<TransactionResponseV2 | null>(null);
	const [filtersOpen, setFiltersOpen] = useState(false);

	const showExpenses = useCallback(() => {
		setView("expenses");
		setFormMode("create");
		setEditingTransaction(null);
	}, []);

	const showAddExpense = useCallback(() => {
		setView("form");
		setFormMode("create");
		setEditingTransaction(null);
	}, []);

	const showUpdateExpense = useCallback((transaction: TransactionResponseV2) => {
		setView("form");
		setFormMode("update");
		setEditingTransaction(transaction);
	}, []);

	const value = useMemo(
		() => ({
			view,
			formMode,
			editingTransaction,
			filtersOpen,
			setFiltersOpen,
			showExpenses,
			showAddExpense,
			showUpdateExpense,
		}),
		[
			view,
			formMode,
			editingTransaction,
			filtersOpen,
			showExpenses,
			showAddExpense,
			showUpdateExpense,
		]
	);

	return (
		<PlannerContext.Provider value={value}>{children}</PlannerContext.Provider>
	);
}

export function usePlanner() {
	const context = useContext(PlannerContext);

	if (!context) {
		throw new Error("usePlanner must be used within PlannerProvider");
	}

	return context;
}
