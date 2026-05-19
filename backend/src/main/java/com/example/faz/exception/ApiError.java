package com.example.faz.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiError {
	private LocalDateTime timestamp;
	private int status;
	private String message;
	private Map<String, String> fieldErrors;

	public ApiError(LocalDateTime timestamp, int status, String message, Map<String, String> fieldErrors) {
		this.timestamp = timestamp;
		this.status = status;
		this.message = message;
		this.fieldErrors = fieldErrors;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, String> getFieldErrors() {
		return fieldErrors;
	}
}
