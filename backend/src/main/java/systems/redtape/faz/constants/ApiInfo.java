package systems.redtape.faz.constants;

public class ApiInfo {
	/** OpenAPI response description for 404. */
	public static final String ERR_NOT_FOUND = "Transaction was not found.";
	public static final String ERR_VALIDATION_FAILED = ValidationMessages.VALIDATION_SUMMARY;
	public static final String ERR_ENDPOINT_NOT_FOUND = "The requested endpoint was not found.";
	public static final String ERR_METHOD_NOT_ALLOWED = "The HTTP method is not allowed for this endpoint.";
	public static final String ERR_INTERNAL = "The request could not be completed.";

	public static final String HTTP_STATUS_OK = "200";
	public static final String HTTP_STATUS_BAD_REQUEST = "400";
	public static final String HTTP_STATUS_NOT_FOUND = "404";

	public static String notFound(Long id) {
		return "Transaction " + id + " was not found.";
	}
}
