package com.paywallet.core.domain.repository;

import com.paywallet.core.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByIdempotencyKey(UUID idempotencyKey);
}
