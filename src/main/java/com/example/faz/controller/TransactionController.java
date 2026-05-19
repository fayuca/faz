package com.example.faz.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.faz.dto.TransactionCriteria;
import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.exception.ApiError;
import com.example.faz.exception.ApiErrors;
import com.example.faz.service.TransactionService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	private final TransactionService service;

	public TransactionController(TransactionService service) {
		this.service = service;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses({
			@ApiResponse(responseCode = ApiErrors.HTTP_STATUS_NOT_FOUND, description = ApiErrors.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	@GetMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = ApiErrors.HTTP_STATUS_NOT_FOUND, description = ApiErrors.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public TransactionResponse get(@PathVariable Long id) {
		return service.get(id);
	}

	@GetMapping
	public Page<TransactionResponse> getAll(@ModelAttribute TransactionCriteria criteria, Pageable pageable) {
		return service.getAll(criteria, pageable);
	}

	@PostMapping
	@ApiResponses({
			@ApiResponse(responseCode = ApiErrors.HTTP_STATUS_BAD_REQUEST, description = ApiErrors.ERR_VALIDATION_FAILED, content = @Content(schema = @Schema(implementation = ApiError.class)))

	})
	public TransactionResponse create(@RequestBody @Valid TransactionRequest dto) {
		return service.create(dto);
	}

	@PutMapping("/{id}")
	@ApiResponses({
			@ApiResponse(responseCode = ApiErrors.HTTP_STATUS_BAD_REQUEST, description = ApiErrors.ERR_VALIDATION_FAILED, content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = ApiErrors.HTTP_STATUS_NOT_FOUND, description = ApiErrors.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public TransactionResponse update(@PathVariable Long id, @RequestBody @Valid TransactionRequest dto) {
		return service.update(id, dto);
	}
}
