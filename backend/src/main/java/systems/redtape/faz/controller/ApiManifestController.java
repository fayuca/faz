package systems.redtape.faz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.dto.api.ApiManifest;
import systems.redtape.faz.service.ApiManifestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Manifest")
@RequestMapping(ApiPaths.V1)
public class ApiManifestController {
	private final ApiManifestService manifestService;

	public ApiManifestController(ApiManifestService manifestService) {
		this.manifestService = manifestService;
	}

	@GetMapping("/manifest")
	@Operation(
			summary = "Get API manifest",
			description = "Contract metadata describing available resources, verbs, and versions")
	public ApiManifest getManifest() {
		return manifestService.getManifest();
	}
}
