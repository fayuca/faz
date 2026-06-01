package systems.redtape.faz.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import systems.redtape.faz.constants.ApiInfo;
import systems.redtape.faz.constants.ValidationMessages;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
	public ResponseEntity<ApiError> handleValidation(Exception ex) {
		BindingResult bindingResult = ex instanceof MethodArgumentNotValidException methodArgumentNotValidException
				? methodArgumentNotValidException.getBindingResult()
				: ((BindException) ex).getBindingResult();

		return ResponseEntity.badRequest().body(validationApiError(bindingResult));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		String message = ValidationMessages.BODY_UNREADABLE;

		if (ex.getCause() instanceof DateTimeParseException) {
			fieldErrors.put("date", ValidationMessages.DATE_INVALID);
			message = ValidationMessages.VALIDATION_SUMMARY;
		}

		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				message,
				fieldErrors);

		return ResponseEntity.badRequest().body(apiError);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> resourceNotFound(ResourceNotFoundException ex) {
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				ex.getMessage(),
				Map.of());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
	public ResponseEntity<ApiError> handleEndpointNotFound() {
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				ApiInfo.ERR_ENDPOINT_NOT_FOUND,
				Map.of());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiError> handleMethodNotAllowed() {
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				HttpStatus.METHOD_NOT_ALLOWED.value(),
				ApiInfo.ERR_METHOD_NOT_ALLOWED,
				Map.of());

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiError);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleInternal(Exception ex) {
		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ApiInfo.ERR_INTERNAL,
				Map.of());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}

	private ApiError validationApiError(BindingResult bindingResult) {
		Map<String, String> fieldErrors = new HashMap<>();

		for (FieldError error : bindingResult.getFieldErrors()) {
			fieldErrors.put(error.getField(), error.getDefaultMessage());
		}

		return new ApiError(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				ApiInfo.ERR_VALIDATION_FAILED,
				fieldErrors);
	}
}
