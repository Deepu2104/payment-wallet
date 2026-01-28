package com.paywallet.core.infrastructure.messaging;

import com.paywallet.core.application.service.TransferService;
import com.paywallet.core.infrastructure.messaging.event.TransactionInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionConsumer {

    private final TransferService transferService;

    @EventListener
    @KafkaListener(topics = "transaction-initiated", groupId = "fraud-group")
    public void consumeTransactionInitiated(TransactionInitiatedEvent event) {
        log.info("Fraud check for txn: {}", event.getTransactionId());

        boolean approved = true;
        String reason = null;

        // Simple Rule: Amount > 10000 -> Reject
        if (event.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            approved = false;
            reason = "High Value Transaction - Manual Review Required";
        }

        try {
            transferService.completeTransfer(event.getTransactionId(), approved, reason);
        } catch (Exception e) {
            log.error("Error completing transfer for txn {}", event.getTransactionId(), e);
            // In production, we might want to retry or dead-letter
        }
    }
}
