import { TRANSACTIONS_PATH } from "../../api/paths";
import type { TransactionApiScope } from "../../api/transactions";

/** Planner uses the v2 transaction contract (includes currency). */
export const PLANNER_API_SCOPE: TransactionApiScope & { version: "v2" } = {
	version: "v2",
	resourcePath: TRANSACTIONS_PATH,
};
