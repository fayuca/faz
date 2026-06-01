package systems.redtape.faz.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import systems.redtape.faz.dto.api.ApiManifest;
import systems.redtape.faz.dto.api.ApiOperation;
import systems.redtape.faz.dto.api.ApiRequestBody;

/**
 * Mirrors explorer {@code validateManifestVersionCoverage.ts} — fails CI when a
 * declared operation version lacks manifest contract slices.
 */
public final class ApiManifestVersionCoverage {
	private static final Set<String> WRITE_VERBS = Set.of("POST", "PUT");

	private ApiManifestVersionCoverage() {
	}

	public static List<String> issues(ApiManifest manifest) {
		List<String> issues = new ArrayList<>();

		for (var resource : manifest.getResources()) {
			for (ApiOperation operation : resource.getOperations()) {
				for (String version : operation.getVersions()) {
					for (String gap : contractGaps(operation, version)) {
						issues.add(
								resource.getId() + " " + operation.getVerb() + " " + version + ": missing " + gap);
					}
				}
			}
		}

		return issues;
	}

	private static List<String> contractGaps(ApiOperation operation, String version) {
		List<String> gaps = new ArrayList<>();

		if ("GET".equals(operation.getVerb()) && !hasQueryParams(operation.getQueryParams(), version)) {
			gaps.add("GET queryParams." + version);
		}

		if (WRITE_VERBS.contains(operation.getVerb())
				&& !hasRequestBody(operation.getRequestBody(), version)) {
			gaps.add(operation.getVerb() + " requestBody." + version);
		}

		return gaps;
	}

	private static boolean hasQueryParams(Map<String, Map<String, Object>> queryParams, String version) {
		return queryParams != null && queryParams.containsKey(version);
	}

	private static boolean hasRequestBody(Map<String, ApiRequestBody> requestBody, String version) {
		return requestBody != null && requestBody.containsKey(version);
	}
}
