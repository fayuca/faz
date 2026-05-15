package com.example.faz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.faz.util.StringResponse;

@RestController
public class PingController {
	@GetMapping("/ping")
	public StringResponse ping() {
		return new StringResponse("pong");
	}

}
