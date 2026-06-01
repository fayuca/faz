package systems.redtape.faz.integration;

import static systems.redtape.faz.support.MockMvcJsonSupport.doPostJson;
import static systems.redtape.faz.support.TransactionTestFixtures.requestV2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.dto.Currency;
import systems.redtape.faz.dto.TransactionResponseV2;
import systems.redtape.faz.repository.TransactionRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionV2IntegrationTest {
	private static final String TRANSACTIONS = ApiPaths.V2_TRANSACTIONS;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransactionRepository repository;

	@Test
	void shouldCreateWithCurrency() throws Exception {
		var result = doPostJson(
				mockMvc,
				objectMapper,
				TRANSACTIONS,
				requestV2(new BigDecimal("50.00"), "EUR lunch", Currency.EUR))
				.andExpect(status().isOk())
				.andReturn();

		TransactionResponseV2 response = objectMapper.readValue(
				result.getResponse().getContentAsString(),
				TransactionResponseV2.class);

		assertEquals(Currency.EUR, response.getCurrency());
		assertEquals(Currency.EUR, repository.findById(response.getId()).orElseThrow().getCurrency());
	}
}
