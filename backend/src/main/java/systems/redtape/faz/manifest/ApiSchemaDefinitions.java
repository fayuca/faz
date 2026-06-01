package systems.redtape.faz.manifest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import systems.redtape.faz.dto.Currency;
import systems.redtape.faz.dto.TransactionCategory;

/**
 * JSON Schema fragments for the API manifest. Field rules align with
 * {@link systems.redtape.faz.dto.TransactionRequest} and
 * {@link systems.redtape.faz.dto.TransactionCriteria}.
 */
public final class ApiSchemaDefinitions {
	public static final String JSON_CONTENT_TYPE = "application/json";

	private ApiSchemaDefinitions() {
	}

	public static Map<String, Object> transactionRequestV1Schema() {
		Map<String, Object> schema = new LinkedHashMap<>();
		schema.put("type", "object");
		schema.put("required", List.of("date", "amount", "description", "category"));

		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put("date", Map.of(
				"type", "string",
				"format", "date-time"));
		properties.put("amount", Map.of(
				"type", "number",
				"exclusiveMinimum", 0));
		properties.put("description", Map.of(
				"type", "string",
				"minLength", 1));
		properties.put("category", Map.of(
				"type", "string",
				"enum", transactionCategoryValues()));

		schema.put("properties", properties);
		return schema;
	}

	public static Map<String, Object> transactionRequestV2Schema() {
		Map<String, Object> schema = new LinkedHashMap<>();
		schema.put("type", "object");
		schema.put("required", List.of("date", "amount", "description", "category", "currency"));

		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put("date", Map.of(
				"type", "string",
				"format", "date-time"));
		properties.put("amount", Map.of(
				"type", "number",
				"exclusiveMinimum", 0));
		properties.put("description", Map.of(
				"type", "string",
				"minLength", 1));
		properties.put("category", Map.of(
				"type", "string",
				"enum", transactionCategoryValues()));
		properties.put("currency", Map.of(
				"type", "string",
				"enum", currencyValues()));

		schema.put("properties", properties);
		return schema;
	}

	public static Map<String, Object> transactionListQueryParams() {
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("description", Map.of("type", "string"));
		params.put("category", Map.of(
				"type", "string",
				"enum", transactionCategoryValues()));
		params.put("from", Map.of("type", "string", "format", "date"));
		params.put("to", Map.of("type", "string", "format", "date"));
		params.put("minAmount", Map.of("type", "number", "minimum", 0));
		params.put("maxAmount", Map.of("type", "number", "minimum", 0));
		params.put("page", Map.of("type", "integer", "minimum", 0));
		params.put("size", Map.of("type", "integer", "minimum", 1));
		params.put("sort", Map.of(
				"type", "string",
				"description", "property,direction e.g. date,desc"));
		return params;
	}

	public static Map<String, Object> transactionListQueryParamsV2() {
		Map<String, Object> params = new LinkedHashMap<>(transactionListQueryParams());
		params.put("currency", Map.of(
				"type", "string",
				"enum", currencyValues()));
		return params;
	}

	public static Map<String, Object> transactionIdPathParam() {
		return Map.of("id", Map.of("type", "integer", "minimum", 1));
	}

	public static List<String> transactionCategoryValues() {
		return List.of(
				TransactionCategory.ENTERTAINMENT.name(),
				TransactionCategory.FOOD.name(),
				TransactionCategory.TRANSPORT.name(),
				TransactionCategory.UTILITIES.name(),
				TransactionCategory.OTHER.name());
	}

	public static List<String> currencyValues() {
		return List.of(Currency.USD.name(), Currency.EUR.name());
	}
}
