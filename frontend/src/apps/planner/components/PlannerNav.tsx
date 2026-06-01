import { Button } from "../../../ui";
import { usePlanner } from "../plannerContext";

export default function PlannerNav() {
	const { view, showAddExpense, showExpenses } = usePlanner();

	return (
		<nav className="planner__nav" aria-label="Planner views">
			<Button
				type="button"
				className={view === "expenses" ? "faz-btn--active" : undefined}
				onClick={showExpenses}
			>
				Expenses
			</Button>
			<Button
				type="button"
				className={view === "form" ? "faz-btn--active" : undefined}
				onClick={showAddExpense}
			>
				Add expense
			</Button>
		</nav>
	);
}
