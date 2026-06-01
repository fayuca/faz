package systems.redtape.faz.dto.api;

import java.util.List;

public class ApiResource {
	private String id;
	private String label;
	private String path;
	private List<ApiOperation> operations;

	public ApiResource(String id, String label, String path, List<ApiOperation> operations) {
		this.id = id;
		this.label = label;
		this.path = path;
		this.operations = operations;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<ApiOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<ApiOperation> operations) {
		this.operations = operations;
	}
}
