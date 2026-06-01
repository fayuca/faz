import { useEffect, useState } from "react";
import type { ApiParamMap } from "../../../api/manifest";
import type { ManifestQueryParams } from "../paramValues";
import RequestPathRow from "../components/RequestPathRow";
import GetListForm from "../components/GetListForm";
import ParamFields from "../components/ParamFields";
import { Button, Panel } from "../../../ui";
import { emptyParamValues, paramsFromManifestValues } from "../paramValues";
import { parsePathParam } from "../validateManifestSchema";
import type { RequestViewProps } from "../types";

type GetMode = "list" | "byId";

type Props = RequestViewProps & {
	queryParams?: ApiParamMap;
	pathParams?: ApiParamMap;
	onSend?: (params: ManifestQueryParams) => void;
	onSendById?: (id: number) => void;
	onClientError?: (message: string) => void;
	clientError?: string | null;
	loading?: boolean;
};

export default function ReadRequestView({
	method,
	resourcePath,
	queryParams,
	pathParams,
	versions,
	apiVersion,
	onVersionChange,
	onSend,
	onSendById,
	onClientError,
	clientError,
	loading,
}: Props) {
	const [getMode, setGetMode] = useState<GetMode>("list");
	const [queryValues, setQueryValues] = useState(() =>
		emptyParamValues(queryParams)
	);
	const [pathValues, setPathValues] = useState(() =>
		emptyParamValues(pathParams)
	);

	useEffect(() => {
		setQueryValues(emptyParamValues(queryParams));
	}, [queryParams, apiVersion]);

	const canSend =
		getMode === "list" ? Boolean(onSend) : Boolean(onSendById);

	function handleQueryChange(name: string, value: string) {
		setQueryValues(current => ({ ...current, [name]: value }));
	}

	function handlePathChange(name: string, value: string) {
		setPathValues(current => ({ ...current, [name]: value }));
	}

	function handleSend() {
		if (getMode === "byId") {
			const idResult = parsePathParam(
				pathValues.id ?? "",
				pathParams?.id,
				"id"
			);
			if (idResult.ok === false) {
				onClientError?.(idResult.message);
				return;
			}

			onSendById?.(idResult.value);
			return;
		}

		onSend?.(paramsFromManifestValues(queryParams, queryValues));
	}

	return (
		<Panel title="Request">
			<RequestPathRow
				method={method}
				resourcePath={resourcePath}
				pathSuffix={getMode === "byId" ? "/{id}" : undefined}
				versions={versions}
				activeVersion={apiVersion}
				onVersionChange={onVersionChange}
			/>

			<div
				className="explorer__get-mode"
				role="tablist"
				aria-label="GET request type"
			>
				<Button
					type="button"
					className={
						getMode === "list"
							? "faz-btn--active explorer__get-mode-btn"
							: "explorer__get-mode-btn"
					}
					onClick={() => setGetMode("list")}
					role="tab"
					aria-selected={getMode === "list"}
				>
					List
				</Button>
				<Button
					type="button"
					className={
						getMode === "byId"
							? "faz-btn--active explorer__get-mode-btn"
							: "explorer__get-mode-btn"
					}
					onClick={() => setGetMode("byId")}
					role="tab"
					aria-selected={getMode === "byId"}
				>
					By ID
				</Button>
			</div>

			{getMode === "byId" ? (
				<ParamFields
					params={pathParams}
					values={pathValues}
					onChange={handlePathChange}
					idPrefix="g"
				/>
			) : (
				<GetListForm
					queryParams={queryParams}
					values={queryValues}
					onChange={handleQueryChange}
				/>
			)}

			<div className="faz-actions">
				<Button
					variant="primary"
					onClick={handleSend}
					disabled={loading || !canSend}
				>
					Send
				</Button>
			</div>

			{clientError && <p className="explorer__request-error">{clientError}</p>}
		</Panel>
	);
}
