package systems.redtape.faz.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import systems.redtape.faz.constants.ApiResources;
import systems.redtape.faz.constants.ApiVersions;
import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.dto.api.ApiManifest;
import systems.redtape.faz.dto.api.ApiOperation;
import systems.redtape.faz.dto.api.ApiRequestBody;
import systems.redtape.faz.dto.api.ApiResource;
import systems.redtape.faz.manifest.ApiSchemaDefinitions;

@Service
public class ApiManifestService {
	private static final Map<String, ApiRequestBody> TRANSACTION_REQUEST_BODIES = Map.of(
			ApiVersions.V1,
			new ApiRequestBody(
					ApiSchemaDefinitions.JSON_CONTENT_TYPE,
					ApiSchemaDefinitions.transactionRequestV1Schema()),
			ApiVersions.V2,
			new ApiRequestBody(
					ApiSchemaDefinitions.JSON_CONTENT_TYPE,
					ApiSchemaDefinitions.transactionRequestV2Schema()));
	private static final Map<String, Map<String, Object>> TRANSACTION_LIST_QUERY_PARAMS = Map.of(
			ApiVersions.V1,
			ApiSchemaDefinitions.transactionListQueryParams(),
			ApiVersions.V2,
			ApiSchemaDefinitions.transactionListQueryParamsV2());

	public ApiManifest getManifest() {
		return new ApiManifest(
				ApiVersions.MANIFEST_CONTRACT,
				List.of(transactionsResource()));
	}

	private ApiResource transactionsResource() {
		return new ApiResource(
				ApiResources.TRANSACTIONS_ID,
				ApiResources.TRANSACTIONS_LABEL,
				ApiPaths.TRANSACTIONS,
				List.of(
						new ApiOperation(
								"GET",
								ApiVersions.V1_AND_V2,
								TRANSACTION_LIST_QUERY_PARAMS,
								ApiSchemaDefinitions.transactionIdPathParam(),
								null),
						new ApiOperation("POST", ApiVersions.V1_AND_V2, null, null, TRANSACTION_REQUEST_BODIES),
						new ApiOperation(
								"PUT",
								ApiVersions.V1_AND_V2,
								null,
								ApiSchemaDefinitions.transactionIdPathParam(),
								TRANSACTION_REQUEST_BODIES),
						new ApiOperation(
								"DELETE",
								ApiVersions.V1_AND_V2,
								null,
								ApiSchemaDefinitions.transactionIdPathParam(),
								null)));
	}
}
