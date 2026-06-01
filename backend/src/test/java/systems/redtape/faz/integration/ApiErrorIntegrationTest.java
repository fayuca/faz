package systems.redtape.faz.integration;

import static systems.redtape.faz.support.ApiErrorAssertions.assertApiError;
import static systems.redtape.faz.support.MockMvcJsonSupport.apiError;
import static systems.redtape.faz.support.MockMvcJsonSupport.doGet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import systems.redtape.faz.constants.ApiInfo;
import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.exception.ApiError;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiErrorIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	// -- TESTS

	@Test
	void rootRedirectsToSwaggerUi() throws Exception {
		doGet(mockMvc, "/")
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/swagger-ui/index.html"));
	}

	@Test
	void unknownApiEndpointReturnsJsonNotFound() throws Exception {
		// *

		MvcResult result = doGet(mockMvc, "/api/v1/does-not-exist")
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();

		// 1

		ApiError apiError = apiError(objectMapper, result);
		assertEquals(404, apiError.getStatus());
		assertApiError(apiError, ApiInfo.ERR_ENDPOINT_NOT_FOUND);
	}

	@Test
	void wrongMethodReturnsJsonMethodNotAllowed() throws Exception {
		// *

		MvcResult result = mockMvc.perform(post(ApiPaths.V1_MANIFEST).accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isMethodNotAllowed())
				.andReturn();

		// 1

		ApiError apiError = apiError(objectMapper, result);
		assertEquals(405, apiError.getStatus());
		assertApiError(apiError, ApiInfo.ERR_METHOD_NOT_ALLOWED);
	}
}
