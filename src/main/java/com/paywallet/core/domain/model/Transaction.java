package com.paywallet.core.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Transaction record for money transfers")
public class Transaction extends BaseEntity {

    @Column(name = "sender_wallet_id", nullable = false)
    @Schema(description = "ID of the sender's wallet")
    private UUID senderWalletId;

    @Column(name = "receiver_wallet_id", nullable = false)
    @Schema(description = "ID of the receiver's wallet")
    private UUID receiverWalletId;

    @Column(nullable = false, precision = 19, scale = 4)
    @Schema(description = "Transfer amount", example = "50.00")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Current status of the transaction")
    private TransactionStatus status;

    @Column
    @Schema(description = "Reason for failure, if any")
    private String failureReason;

    @Column(unique = true)
    @Schema(description = "Idempotency key for request deduplication")
    private UUID idempotencyKey;

    public enum TransactionStatus {
        PENDING,
        FRAUD_CHECK,
        SUCCESS,
        FAILED
    }
}
