package com.paywallet.core.domain.repository;

import com.paywallet.core.domain.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
