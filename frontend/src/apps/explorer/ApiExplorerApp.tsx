import { useEffect, useState } from "react";
import { PageHeader } from "../../ui";
import type { HttpMethod } from "../../ui";
import type { ApiVersion } from "../../api/paths";
import {
	clampApiVersion,
	fetchApiManifest,
	operationForVerb,
	queryParamsForVersion,
	requestBodySchemaForVersion,
	resourceById,
	type ApiManifest,
	verbsForResource,
	versionsForVerb,
} from "../../api/manifest";
import {
	formatManifestVersionCoverageWarning,
	manifestVersionCoverageIssues,
} from "./validateManifestVersionCoverage";
import "./explorer.css";
import RequestPanel from "./components/RequestPanel";
import ResourceList from "./components/ResourceList";
import ResponsePanel from "./components/ResponsePanel";
import VerbBar from "./components/VerbBar";
import { useExplorerRequest } from "./hooks/useExplorerRequest";

export default function ApiExplorerApp() {
	const [manifest, setManifest] = useState<ApiManifest | null>(null);
	const [manifestWarning, setManifestWarning] = useState<string | null>(null);
	const [loadError, setLoadError] = useState<string | null>(null);
	const [activeResourceId, setActiveResourceId] = useState("");
	const [method, setMethod] = useState<HttpMethod>("GET");
	const [apiVersion, setApiVersion] = useState<ApiVersion>("v1");

	useEffect(() => {
		let cancelled = false;

		fetchApiManifest()
			.then(data => {
				if (cancelled) {
					return;
				}

				const first = data.resources[0];
				if (!first) {
					setLoadError("The API manifest has no resources.");
					return;
				}

				setManifest(data);
				const coverageIssues = manifestVersionCoverageIssues(data);
				const warning = formatManifestVersionCoverageWarning(coverageIssues);
				setManifestWarning(warning || null);
				if (warning) {
					console.warn(warning);
				}
				setActiveResourceId(first.id);
				setMethod(verbsForResource(first)[0]);
				setApiVersion(versionsForVerb(first, verbsForResource(first)[0])[0]);
			})
			.catch(() => {
				if (!cancelled) {
					setLoadError("Could not load the API manifest.");
				}
			});

		return () => {
			cancelled = true;
		};
	}, []);

	const defaultResource = manifest?.resources[0];
	const resource =
		(manifest && resourceById(manifest, activeResourceId)) ?? defaultResource;
	const availableVersions = resource
		? versionsForVerb(resource, method)
		: (["v1"] as const);
	const activeOperation = resource
		? operationForVerb(resource, method)
		: undefined;
	const bodySchema = requestBodySchemaForVersion(activeOperation, apiVersion);
	const queryParams = queryParamsForVersion(activeOperation, apiVersion);
	const pathParams = activeOperation?.pathParams;

	const {
		response,
		clientError,
		loading,
		sendListRequest,
		sendGetByIdRequest,
		sendCreateRequest,
		sendUpdateRequest,
		sendDeleteRequest,
		reportClientError,
	} = useExplorerRequest({
		version: apiVersion,
		resourcePath: resource?.path ?? "/transactions",
	});

	function selectResource(id: string) {
		if (!manifest) {
			return;
		}

		const next = resourceById(manifest, id);
		if (!next) {
			return;
		}

		const nextVerbs = verbsForResource(next);
		const nextMethod = nextVerbs.includes(method) ? method : nextVerbs[0];
		setActiveResourceId(id);
		setMethod(nextMethod);
		setApiVersion(current =>
			clampApiVersion(versionsForVerb(next, nextMethod), current)
		);
	}

	function selectMethod(next: HttpMethod) {
		if (!resource) {
			return;
		}

		setMethod(next);
		setApiVersion(current =>
			clampApiVersion(versionsForVerb(resource, next), current)
		);
	}

	if (loadError) {
		return (
			<div className="explorer">
				<PageHeader
					title="API Explorer"
					subtitle="Pseudo-swagger UI for the faz REST API."
				/>
				<p className="explorer__request-error">{loadError}</p>
			</div>
		);
	}

	if (!manifest || !resource) {
		return (
			<div className="explorer">
				<PageHeader
					title="API Explorer"
					subtitle="Pseudo-swagger UI for the faz REST API."
				/>
				<p>Loading manifest…</p>
			</div>
		);
	}

	return (
		<div className="explorer">
			<PageHeader
				title="API Explorer"
				subtitle="Pseudo-swagger UI for the faz REST API."
			/>

			{manifestWarning && (
				<pre className="explorer__manifest-warning faz-scroll-host">
					{manifestWarning}
				</pre>
			)}

			<div className="explorer__layout">
				<ResourceList
					resources={manifest.resources}
					activeId={resource.id}
					onSelect={selectResource}
				/>

				<div className="explorer__main-stack">
					<VerbBar
						verbs={verbsForResource(resource)}
						activeVerb={method}
						onSelect={selectMethod}
					/>
					<RequestPanel
						method={method}
						resourcePath={resource.path}
						versions={availableVersions}
						apiVersion={apiVersion}
						bodySchema={bodySchema}
						queryParams={queryParams}
						pathParams={pathParams}
						onVersionChange={setApiVersion}
						onSendList={sendListRequest}
						onSendById={sendGetByIdRequest}
						onSendCreate={sendCreateRequest}
						onSendUpdate={sendUpdateRequest}
						onSendDelete={sendDeleteRequest}
						onClientError={reportClientError}
						clientError={clientError}
						loading={loading}
					/>
					<ResponsePanel response={response} loading={loading} />
				</div>
			</div>
		</div>
	);
}
