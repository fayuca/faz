import { useEffect, useMemo, useState } from "react";
import RequestPathRow from "../components/RequestPathRow";
import { Button, Panel, TextArea } from "../../../ui";
import ParamFields from "../components/ParamFields";
import { emptyParamValues } from "../paramValues";
import type { HttpMethod } from "../../../ui";
import type { JsonSchema, ApiParamMap } from "../../../api/manifest";
import { synthesizePlaceholder } from "../synthesizePlaceholder";
import { ExplorerMessages } from "../explorerMessages";
import {
	validateRequestBody,
	parsePathParam,
	type ValidatedBody,
} from "../validateManifestSchema";
import type { RequestViewProps } from "../types";

type Props = RequestViewProps & {
	method: Extract<HttpMethod, "POST" | "PUT">;
	pathSuffix?: string;
	pathParams?: ApiParamMap;
	bodySchema?: JsonSchema;
	onSend?: (request: ValidatedBody) => void;
	onSendUpdate?: (id: number, request: ValidatedBody) => void;
	onClientError?: (message: string) => void;
	clientError?: string | null;
	loading?: boolean;
};

function parseBody(
	body: string,
	bodySchema: JsonSchema | undefined,
	onClientError?: (message: string) => void
): ValidatedBody | undefined {
	let parsed: unknown;

	try {
		parsed = JSON.parse(body);
	} catch {
		onClientError?.(ExplorerMessages.jsonInvalid);
		return undefined;
	}

	const result = validateRequestBody(parsed, bodySchema);
	if (result.ok === false) {
		onClientError?.(result.message);
		return undefined;
	}

	return result.value;
}

export default function WriteRequestView({
	method,
	resourcePath,
	pathSuffix,
	pathParams,
	bodySchema,
	versions,
	apiVersion,
	onVersionChange,
	onSend,
	onSendUpdate,
	onClientError,
	clientError,
	loading,
}: Props) {
	const placeholder = useMemo(
		() => (bodySchema ? synthesizePlaceholder(bodySchema) : ""),
		[bodySchema]
	);
	const [pathValues, setPathValues] = useState(() =>
		emptyParamValues(pathParams)
	);
	const [body, setBody] = useState("");

	useEffect(() => {
		setBody(bodySchema ? synthesizePlaceholder(bodySchema) : "");
	}, [bodySchema, apiVersion]);

	function handleSend() {
		const request = parseBody(body, bodySchema, onClientError);
		if (!request) {
			return;
		}

		if (method === "PUT") {
			const idResult = parsePathParam(
				pathValues.id ?? "",
				pathParams?.id,
				"id"
			);
			if (idResult.ok === false) {
				onClientError?.(idResult.message);
				return;
			}

			onSendUpdate?.(idResult.value, request);
			return;
		}

		onSend?.(request);
	}

	const canSend =
		method === "POST" ? Boolean(onSend) : Boolean(onSendUpdate);

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

			{method === "PUT" && (
				<ParamFields
					params={pathParams}
					values={pathValues}
					onChange={(name, value) =>
						setPathValues(current => ({ ...current, [name]: value }))
					}
					idPrefix="w"
				/>
			)}

			<div>
				<label className="faz-field-label" htmlFor="w-body">
					body (JSON)
				</label>
				<div className="faz-textarea-wrap faz-scroll-host">
					<TextArea
						id="w-body"
						placeholder={placeholder}
						value={body}
						onChange={e => setBody(e.target.value)}
					/>
				</div>
			</div>

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
