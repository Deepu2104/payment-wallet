package com.paywallet.core.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerEntry extends BaseEntity {

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId; // Link to the transfer/transaction

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LegacyType type; // DEBIT or CREDIT

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal balanceAfter; // Snapshot of balance for audit

    public enum LegacyType {
        DEBIT, CREDIT
    }
}
