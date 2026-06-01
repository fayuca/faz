package systems.redtape.faz.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import systems.redtape.faz.constants.ApiInfo;
import systems.redtape.faz.dto.TransactionCriteria;
import systems.redtape.faz.dto.TransactionRequest;
import systems.redtape.faz.dto.TransactionRequestV2;
import systems.redtape.faz.dto.TransactionResponse;
import systems.redtape.faz.dto.TransactionResponseV2;
import systems.redtape.faz.entity.Transaction;
import systems.redtape.faz.exception.ResourceNotFoundException;
import systems.redtape.faz.repository.TransactionRepository;
import systems.redtape.faz.specification.TransactionSpecifications;

@Service
public class TransactionService {
	private final TransactionRepository repository;

	public TransactionService(TransactionRepository repository) {
		this.repository = repository;
	}

	public TransactionResponse create(TransactionRequest request) {
		Transaction transaction = Transaction.from(request);
		Transaction saved = repository.save(transaction);
		return saved.response();
	}

	public TransactionResponseV2 createV2(TransactionRequestV2 request) {
		Transaction transaction = Transaction.fromV2(request);
		Transaction saved = repository.save(transaction);
		return saved.responseV2();
	}

	public void delete(Long id) {
		repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ApiInfo.notFound(id)));
		repository.deleteById(id);
	}

	public TransactionResponse get(Long id) throws ResourceNotFoundException {
		Transaction saved = requireTransaction(id);
		return saved.response();
	}

	public TransactionResponseV2 getV2(Long id) throws ResourceNotFoundException {
		Transaction saved = requireTransaction(id);
		return saved.responseV2();
	}

	public Page<TransactionResponse> getAll(TransactionCriteria criteria, Pageable pageable) {
		return repository
				.findAll(TransactionSpecifications.withCriteria(criteria), pageable)
				.map(Transaction::response);
	}

	public Page<TransactionResponseV2> getAllV2(TransactionCriteria criteria, Pageable pageable) {
		return repository
				.findAll(TransactionSpecifications.withCriteria(criteria), pageable)
				.map(Transaction::responseV2);
	}

	public TransactionResponse update(Long id, TransactionRequest request) {
		Transaction transaction = requireTransaction(id);

		transaction.setAmount(request.getAmount());
		transaction.setDescription(request.getDescription());
		transaction.setCategory(request.getCategory());
		transaction.setDate(request.getDate());

		Transaction saved = repository.save(transaction);
		return saved.response();
	}

	public TransactionResponseV2 updateV2(Long id, TransactionRequestV2 request) {
		Transaction transaction = requireTransaction(id);
		transaction.applyV2(request);
		Transaction saved = repository.save(transaction);
		return saved.responseV2();
	}

	private Transaction requireTransaction(Long id) {
		return repository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(ApiInfo.notFound(id)));
	}
}
