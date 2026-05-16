package com.example.faz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.faz.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByDescriptionContainingIgnoreCase(String description);
}
