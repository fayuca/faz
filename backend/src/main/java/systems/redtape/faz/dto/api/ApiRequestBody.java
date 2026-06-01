package systems.redtape.faz.dto.api;

import java.util.Map;

public class ApiRequestBody {
	private String contentType;
	private Map<String, Object> schema;

	public ApiRequestBody(String contentType, Map<String, Object> schema) {
		this.contentType = contentType;
		this.schema = schema;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, Object> getSchema() {
		return schema;
	}

	public void setSchema(Map<String, Object> schema) {
		this.schema = schema;
	}
}
