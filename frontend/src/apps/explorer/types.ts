import type { HttpMethod } from "../../ui";
import type { ApiVersion } from "../../api/paths";

export type RequestViewProps = {
	method: HttpMethod;
	resourcePath: string;
	versions: readonly ApiVersion[];
	apiVersion: ApiVersion;
	onVersionChange: (version: ApiVersion) => void;
};

/** HTTP response only — client validation uses `clientError` on the request panel. */
export type ExplorerResponse = {
	status: number;
	body: unknown;
};
