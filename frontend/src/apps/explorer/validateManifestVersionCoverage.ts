import type { HttpMethod } from "../../ui";
import type { ApiVersion } from "../../api/paths";
import type { ApiManifest, ApiOperation } from "../../api/manifest";

const WRITE_VERBS: readonly HttpMethod[] = ["POST", "PUT"];

function contractForVersion(
	operation: ApiOperation,
	version: ApiVersion
): string[] {
	const issues: string[] = [];

	if (operation.verb === "GET" && !operation.queryParams?.[version]) {
		issues.push(`GET queryParams.${version}`);
	}

	if (
		WRITE_VERBS.includes(operation.verb) &&
		!operation.requestBody?.[version]
	) {
		issues.push(`${operation.verb} requestBody.${version}`);
	}

	return issues;
}

/**
 * Returns human-readable gaps when a declared operation version lacks
 * manifest contract slices (query params or request body).
 */
export function manifestVersionCoverageIssues(
	manifest: ApiManifest
): string[] {
	const issues: string[] = [];

	for (const resource of manifest.resources) {
		for (const operation of resource.operations) {
			for (const version of operation.versions) {
				for (const gap of contractForVersion(operation, version)) {
					issues.push(`${resource.id} ${operation.verb} ${version}: missing ${gap}`);
				}
			}
		}
	}

	return issues;
}

export function formatManifestVersionCoverageWarning(
	issues: string[]
): string {
	if (issues.length === 0) {
		return "";
	}

	return [
		"Manifest version coverage gaps (update backend manifest + explorer wiring):",
		...issues.map(issue => `• ${issue}`),
	].join("\n");
}
