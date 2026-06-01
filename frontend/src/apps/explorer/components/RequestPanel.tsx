import type { JsonSchema, ApiParamMap } from "../../../api/manifest";
import type { ManifestQueryParams } from "../paramValues";
import type { HttpMethod } from "../../../ui";
import type { ApiVersion } from "../../../api/paths";
import type { ValidatedBody } from "../validateManifestSchema";
import IdRequestView from "../views/IdRequestView";
import ReadRequestView from "../views/ReadRequestView";
import WriteRequestView from "../views/WriteRequestView";

type Props = {
	method: HttpMethod;
	resourcePath: string;
	versions: readonly ApiVersion[];
	apiVersion: ApiVersion;
	bodySchema?: JsonSchema;
	queryParams?: ApiParamMap;
	pathParams?: ApiParamMap;
	onVersionChange: (version: ApiVersion) => void;
	onSendList?: (params: ManifestQueryParams) => void;
	onSendById?: (id: number) => void;
	onSendCreate?: (request: ValidatedBody) => void;
	onSendUpdate?: (id: number, request: ValidatedBody) => void;
	onSendDelete?: (id: number) => void;
	onClientError?: (message: string) => void;
	clientError?: string | null;
	loading?: boolean;
};

export default function RequestPanel({
	method,
	resourcePath,
	versions,
	apiVersion,
	bodySchema,
	queryParams,
	pathParams,
	onVersionChange,
	onSendList,
	onSendById,
	onSendCreate,
	onSendUpdate,
	onSendDelete,
	onClientError,
	clientError,
	loading,
}: Props) {
	const pathProps = {
		resourcePath,
		versions,
		apiVersion,
		onVersionChange,
	};

	switch (method) {
		case "GET":
			return (
				<ReadRequestView
					key={`GET-${apiVersion}`}
					method="GET"
					{...pathProps}
					queryParams={queryParams}
					pathParams={pathParams}
					onSend={onSendList}
					onSendById={onSendById}
					onClientError={onClientError}
					clientError={clientError}
					loading={loading}
				/>
			);
		case "POST":
			return (
				<WriteRequestView
					key={`POST-${apiVersion}`}
					method="POST"
					{...pathProps}
					bodySchema={bodySchema}
					onSend={onSendCreate}
					onClientError={onClientError}
					clientError={clientError}
					loading={loading}
				/>
			);
		case "PUT":
			return (
				<WriteRequestView
					key={`PUT-${apiVersion}`}
					method="PUT"
					{...pathProps}
					pathSuffix="/{id}"
					pathParams={pathParams}
					bodySchema={bodySchema}
					onSendUpdate={onSendUpdate}
					onClientError={onClientError}
					clientError={clientError}
					loading={loading}
				/>
			);
		case "DELETE":
			return (
				<IdRequestView
					key="DELETE"
					method="DELETE"
					{...pathProps}
					pathSuffix="/{id}"
					pathParams={pathParams}
					onSendDelete={onSendDelete}
					onClientError={onClientError}
					clientError={clientError}
					loading={loading}
				/>
			);
	}
}
