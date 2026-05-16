package com.example.faz.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				ApiErrors.VALIDATION_FAILED,
				fieldErrors);

		return ResponseEntity.badRequest().body(apiError);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> resourceNotFound(ResourceNotFoundException ex) {
		Map<String, String> fieldErrors = new HashMap<>();

		ApiError apiError = new ApiError(
				LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				ex.getMessage(),
				fieldErrors);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}
}
