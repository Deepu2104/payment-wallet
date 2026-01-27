package com.paywallet.core.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInitiatedEvent {
    private UUID transactionId;
    private UUID senderWalletId;
    private UUID receiverWalletId;
    private BigDecimal amount;
    private String timestamp;
}
