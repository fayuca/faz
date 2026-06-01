import { useState } from "react";
import type { ApiParamMap } from "../../../api/manifest";
import RequestPathRow from "../components/RequestPathRow";
import ParamFields from "../components/ParamFields";
import { Button, Panel } from "../../../ui";
import { emptyParamValues } from "../paramValues";
import { parsePathParam } from "../validateManifestSchema";
import type { RequestViewProps } from "../types";

type Props = RequestViewProps & {
	pathSuffix?: string;
	pathParams?: ApiParamMap;
	onSendDelete?: (id: number) => void;
	onClientError?: (message: string) => void;
	clientError?: string | null;
	loading?: boolean;
};

export default function IdRequestView({
	method,
	resourcePath,
	pathSuffix,
	pathParams,
	versions,
	apiVersion,
	onVersionChange,
	onSendDelete,
	onClientError,
	clientError,
	loading,
}: Props) {
	const [pathValues, setPathValues] = useState(() =>
		emptyParamValues(pathParams)
	);

	function handlePathChange(name: string, value: string) {
		setPathValues(current => ({ ...current, [name]: value }));
	}

	function handleSend() {
		const idResult = parsePathParam(pathValues.id ?? "", pathParams?.id, "id");
		if (idResult.ok === false) {
			onClientError?.(idResult.message);
			return;
		}

		onSendDelete?.(idResult.value);
	}

	return (
		<Panel title="Request">
			<RequestPathRow
				method={method}
				resourcePath={resourcePath}
				pathSuffix={pathSuffix}
				versions={versions}
				activeVersion={apiVersion}
				onVersionChange={onVersionChange}
			/>

			<ParamFields
				params={pathParams}
				values={pathValues}
				onChange={handlePathChange}
				idPrefix="i"
			/>

			<div className="faz-actions">
				<Button
					variant="primary"
					onClick={handleSend}
					disabled={loading || !onSendDelete}
				>
					Send
				</Button>
			</div>

			{clientError && <p className="explorer__request-error">{clientError}</p>}
		</Panel>
	);
}
