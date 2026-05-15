package com.example.faz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.faz.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
