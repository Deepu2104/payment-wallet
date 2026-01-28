package com.paywallet.core.infrastructure.messaging;

import com.paywallet.core.infrastructure.messaging.event.TransactionInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private static final String TOPIC_TRANSACTION_INITIATED = "transaction-initiated";

    @Value("${application.features.kafka.enabled:true}")
    private boolean kafkaEnabled;

    public void publishTransactionInitiated(TransactionInitiatedEvent event) {
        log.info("Publishing transaction initiated event: {}", event.getTransactionId());
        if (!kafkaEnabled) {
            log.info("Kafka disabled, publishing internal event for txn: {}", event.getTransactionId());
            eventPublisher.publishEvent(event);
            return;
        }
        kafkaTemplate.send(TOPIC_TRANSACTION_INITIATED, event.getTransactionId().toString(), event);
    }
}
