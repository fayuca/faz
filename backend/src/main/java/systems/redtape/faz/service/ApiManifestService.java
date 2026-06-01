package systems.redtape.faz.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.dto.api.ApiManifest;
import systems.redtape.faz.dto.api.ApiOperation;
import systems.redtape.faz.dto.api.ApiRequestBody;
import systems.redtape.faz.dto.api.ApiResource;
import systems.redtape.faz.manifest.ApiSchemaDefinitions;

@Service
public class ApiManifestService {
	private static final String CONTRACT_VERSION = "v1";
	private static final List<String> V1_AND_V2 = List.of("v1", "v2");
	private static final Map<String, ApiRequestBody> TRANSACTION_REQUEST_BODIES = Map.of(
			"v1",
			new ApiRequestBody(
					ApiSchemaDefinitions.JSON_CONTENT_TYPE,
					ApiSchemaDefinitions.transactionRequestV1Schema()),
			"v2",
			new ApiRequestBody(
					ApiSchemaDefinitions.JSON_CONTENT_TYPE,
					ApiSchemaDefinitions.transactionRequestV2Schema()));
	private static final Map<String, Map<String, Object>> TRANSACTION_LIST_QUERY_PARAMS = Map.of(
			"v1",
			ApiSchemaDefinitions.transactionListQueryParams(),
			"v2",
			ApiSchemaDefinitions.transactionListQueryParamsV2());

	public ApiManifest getManifest() {
		return new ApiManifest(
				CONTRACT_VERSION,
				List.of(transactionsResource()));
	}

	private ApiResource transactionsResource() {
		return new ApiResource(
				"transactions",
				"Transactions",
				ApiPaths.TRANSACTIONS,
				List.of(
						new ApiOperation(
								"GET",
								V1_AND_V2,
								TRANSACTION_LIST_QUERY_PARAMS,
								ApiSchemaDefinitions.transactionIdPathParam(),
								null),
						new ApiOperation("POST", V1_AND_V2, null, null, TRANSACTION_REQUEST_BODIES),
						new ApiOperation(
								"PUT",
								V1_AND_V2,
								null,
								ApiSchemaDefinitions.transactionIdPathParam(),
								TRANSACTION_REQUEST_BODIES),
						new ApiOperation(
								"DELETE",
								V1_AND_V2,
								null,
								ApiSchemaDefinitions.transactionIdPathParam(),
								null)));
	}
}
