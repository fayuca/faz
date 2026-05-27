import { useState } from "react";
import ApiExplorerApp from "./apps/explorer/ApiExplorerApp";
import BudgetPlannerApp from "./apps/planner/BudgetPlannerApp";
import { AppShell } from "./shell/AppShell";
import type { AppId } from "./shell/types";

function App() {
	const [activeApp, setActiveApp] = useState<AppId>("planner");

	return (
		<AppShell
			activeApp={activeApp}
			onAppChange={setActiveApp}
			version={__FAZ_VERSION__}
		>
			{activeApp === "planner" && <BudgetPlannerApp />}
			{activeApp === "explorer" && <ApiExplorerApp />}
		</AppShell>
	);
}

export default App;
