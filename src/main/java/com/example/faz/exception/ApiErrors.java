package com.example.faz.exception;

public class ApiErrors {
	public static final String VALIDATION_FAILED = "validation failed";

	public static String notFound(Long id) {
		return "not found: " + id;
	}
}
