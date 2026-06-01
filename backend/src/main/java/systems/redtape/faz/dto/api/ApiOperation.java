package systems.redtape.faz.dto.api;

import java.util.List;
import java.util.Map;

public class ApiOperation {
	private String verb;
	private List<String> versions;
	private Map<String, Map<String, Object>> queryParams;
	private Map<String, Object> pathParams;
	private Map<String, ApiRequestBody> requestBody;

	public ApiOperation(String verb, List<String> versions) {
		this(verb, versions, null, null, null);
	}

	public ApiOperation(
			String verb,
			List<String> versions,
			Map<String, Map<String, Object>> queryParams,
			Map<String, Object> pathParams,
			Map<String, ApiRequestBody> requestBody) {
		this.verb = verb;
		this.versions = versions;
		this.queryParams = queryParams;
		this.pathParams = pathParams;
		this.requestBody = requestBody;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

	public Map<String, Map<String, Object>> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, Map<String, Object>> queryParams) {
		this.queryParams = queryParams;
	}

	public Map<String, Object> getPathParams() {
		return pathParams;
	}

	public void setPathParams(Map<String, Object> pathParams) {
		this.pathParams = pathParams;
	}

	public Map<String, ApiRequestBody> getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Map<String, ApiRequestBody> requestBody) {
		this.requestBody = requestBody;
	}
}
