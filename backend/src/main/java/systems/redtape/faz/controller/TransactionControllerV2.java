package systems.redtape.faz.controller;

import org.springdoc.core.annotations.ParameterObject;
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

import systems.redtape.faz.constants.ApiInfo;
import systems.redtape.faz.constants.ApiPaths;
import systems.redtape.faz.dto.PageResponse;
import systems.redtape.faz.dto.TransactionCriteria;
import systems.redtape.faz.dto.TransactionRequestV2;
import systems.redtape.faz.dto.TransactionResponseV2;
import systems.redtape.faz.exception.ApiError;
import systems.redtape.faz.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Transactions v2")
@RequestMapping(ApiPaths.V2_TRANSACTIONS)
public class TransactionControllerV2 {
	private final TransactionService service;

	public TransactionControllerV2(TransactionService service) {
		this.service = service;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Delete transaction", description = "Deletes a transaction by its id. Returns no content if successful.")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_NOT_FOUND, description = ApiInfo.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get transaction by id", description = "Retrieves a single transaction by its unique identifier (includes currency).")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_NOT_FOUND, description = ApiInfo.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public TransactionResponseV2 get(@PathVariable Long id) {
		return service.getV2(id);
	}

	@GetMapping
	@Operation(summary = "Get all transactions", description = "Returns a paginated list of transactions with optional filtering (includes currency).")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_OK, description = "Paginated list of transactions"),
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_BAD_REQUEST, description = ApiInfo.ERR_VALIDATION_FAILED, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public PageResponse<TransactionResponseV2> getAll(
			@Valid @ModelAttribute TransactionCriteria criteria,
			@ParameterObject Pageable pageable) {
		return PageResponse.from(service.getAllV2(criteria, pageable));
	}

	@PostMapping
	@Operation(summary = "Create transaction", description = "Creates a new transaction. Required fields: date, amount, description, category, currency.")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_BAD_REQUEST, description = ApiInfo.ERR_VALIDATION_FAILED, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public TransactionResponseV2 create(@RequestBody @Valid TransactionRequestV2 dto) {
		return service.createV2(dto);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update transaction", description = "Updates an existing transaction identified by id (includes currency).")
	@ApiResponses({
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_BAD_REQUEST, description = ApiInfo.ERR_VALIDATION_FAILED, content = @Content(schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = ApiInfo.HTTP_STATUS_NOT_FOUND, description = ApiInfo.ERR_NOT_FOUND, content = @Content(schema = @Schema(implementation = ApiError.class)))
	})
	public TransactionResponseV2 update(@PathVariable Long id, @RequestBody @Valid TransactionRequestV2 dto) {
		return service.updateV2(id, dto);
	}
}
