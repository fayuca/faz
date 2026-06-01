import { Panel } from "../../../ui";
import type { ExplorerResponse } from "../types";

type Props = {
	response: ExplorerResponse | null;
	loading: boolean;
};

export default function ResponsePanel({ response, loading }: Props) {
	return (
		<Panel title="Response">
			{loading && (
				<p className="explorer__response-empty">Sending…</p>
			)}
			{!loading && !response && (
				<p className="explorer__response-empty">No response yet — send a request.</p>
			)}
			{!loading && response && (
				<>
					<p className="explorer__response-meta">
						status: {response.status}
					</p>
					<pre className="explorer__response-body faz-scroll-host faz-scroll-host--surface">
						{JSON.stringify(response.body, null, 2)}
					</pre>
				</>
			)}
		</Panel>
	);
}
