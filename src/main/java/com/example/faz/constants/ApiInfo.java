package com.example.faz.constants;

public class ApiInfo {
	public static final String ERR_NOT_FOUND = "not found";
	public static final String ERR_VALIDATION_FAILED = "validation failed";

	public static final String HTTP_STATUS_OK = "200";
	public static final String HTTP_STATUS_BAD_REQUEST = "400";
	public static final String HTTP_STATUS_NOT_FOUND = "404";

	public static String notFound(Long id) {
		return ERR_NOT_FOUND + " " + id;
	}
}
