package systems.redtape.faz.dto.api;

import java.util.List;

public class ApiManifest {
	private String contractVersion;
	private List<ApiResource> resources;

	public ApiManifest(String contractVersion, List<ApiResource> resources) {
		this.contractVersion = contractVersion;
		this.resources = resources;
	}

	public String getContractVersion() {
		return contractVersion;
	}

	public void setContractVersion(String contractVersion) {
		this.contractVersion = contractVersion;
	}

	public List<ApiResource> getResources() {
		return resources;
	}

	public void setResources(List<ApiResource> resources) {
		this.resources = resources;
	}
}
