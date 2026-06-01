package systems.redtape.faz.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static systems.redtape.faz.support.MockMvcJsonSupport.doGet;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.service.ApiManifestService;
import systems.redtape.faz.support.ApiManifestVersionCoverage;

@WebMvcTest(ApiManifestController.class)
@Import(ApiManifestService.class)
public class ApiManifestControllerTest {
	private static final String MANIFEST = ApiPaths.V1_MANIFEST;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ApiManifestService apiManifestService;

	// -- TESTS

	@Test
	void manifestShouldCoverEveryDeclaredVersion() {
		assertThat(ApiManifestVersionCoverage.issues(apiManifestService.getManifest())).isEmpty();
	}

	@Test
	void shouldReturnManifest() throws Exception {
		// *

		doGet(mockMvc, MANIFEST)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.contractVersion").value("v1"))
				.andExpect(jsonPath("$.resources.length()").value(1))
				.andExpect(jsonPath("$.resources[0].id").value("transactions"))
				.andExpect(jsonPath("$.resources[0].label").value("Transactions"))
				.andExpect(jsonPath("$.resources[0].path").value("/transactions"))
				.andExpect(jsonPath("$.resources[0].operations.length()").value(4))
				.andExpect(jsonPath("$.resources[0].operations[0].verb").value("GET"))
				.andExpect(jsonPath("$.resources[0].operations[0].versions[0]").value("v1"))
				.andExpect(jsonPath("$.resources[0].operations[0].versions[1]").value("v2"))
				.andExpect(jsonPath("$.resources[0].operations[0].queryParams.v1.minAmount.type").value("number"))
				.andExpect(jsonPath("$.resources[0].operations[0].queryParams.v2.currency.enum[0]")
						.value("USD"))
				.andExpect(jsonPath("$.resources[0].operations[0].queryParams.v1.minAmount.minimum").value(0))
				.andExpect(jsonPath("$.resources[0].operations[0].queryParams.v1.maxAmount.type").value("number"))
				.andExpect(jsonPath("$.resources[0].operations[0].pathParams.id.type").value("integer"))
				.andExpect(jsonPath("$.resources[0].operations[1].verb").value("POST"))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v1.contentType")
						.value("application/json"))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v1.schema.type").value("object"))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v1.schema.properties.description.minLength")
						.value(1))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v1.schema.properties.amount.exclusiveMinimum")
						.value(0))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v1.schema.properties.category.enum[1]")
						.value("FOOD"))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v2.contentType")
						.value("application/json"))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v2.schema.properties.currency.enum[0]")
						.value("USD"))
				.andExpect(jsonPath("$.resources[0].operations[1].requestBody.v2.schema.properties.currency.enum[1]")
						.value("EUR"))
				.andExpect(jsonPath("$.resources[0].operations[2].verb").value("PUT"))
				.andExpect(jsonPath("$.resources[0].operations[2].pathParams.id.type").value("integer"))
				.andExpect(jsonPath("$.resources[0].operations[2].requestBody.v1.contentType")
						.value("application/json"))
				.andExpect(jsonPath("$.resources[0].operations[3].verb").value("DELETE"));
	}
}
