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

import com.example.faz.constants.ApiInfo;
import com.example.faz.dto.TransactionCriteria;
import com.example.faz.dto.TransactionRequest;
import com.example.faz.dto.TransactionResponse;
import com.example.faz.exception.ApiError;
import com.example.faz.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Transactions")
@RequestMapping("/api/transactions")
public class TransactionController {
	private final TransactionService service;

	public TransactionController(TransactionService service) {
		this.service = service;
	}

	// --

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete transaction", description = "Deletes a transaction by its id. Returns no content if successful.")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_NOT_FOUND, description = ApiInfo.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	// --

	@GetMapping("/{id}")
	@Operation(summary = "Get transaction by id", description = "Retrieves a single transaction by its unique identifier")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_NOT_FOUND, description = ApiInfo.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public TransactionResponse get(@PathVariable Long id) {
		return service.get(id);
	}

	@GetMapping
	@Operation(summary = "Get all transactions", description = "Returns a paginated list of transactions with optional filtering by criteria")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_OK, description = "Paginated list of transactions")

	})
	public Page<TransactionResponse> getAll(@ModelAttribute TransactionCriteria criteria, Pageable pageable) {
		return service.getAll(criteria, pageable);
	}

	// --

	@PostMapping
	@Operation(summary = "Create transaction", description = "Creates a new transaction with amount, description, and optional category")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_BAD_REQUEST, description = ApiInfo.ERR_VALIDATION_FAILED, content = @Content(schema = @Schema(implementation = ApiError.class)))

	})
	public TransactionResponse create(@RequestBody @Valid TransactionRequest dto) {
		return service.create(dto);
	}

	// --

	@PutMapping("/{id}")
	@Operation(summary = "Update transaction", description = "Updates an existing transaction identified by id")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_BAD_REQUEST, description = ApiInfo.ERR_VALIDATION_FAILED, content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_NOT_FOUND, description = ApiInfo.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public TransactionResponse update(@PathVariable Long id, @RequestBody @Valid TransactionRequest dto) {
		return service.update(id, dto);
	}
}
