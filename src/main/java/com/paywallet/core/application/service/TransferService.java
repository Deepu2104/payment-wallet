package com.paywallet.core.application.service;

import com.paywallet.core.domain.model.Transaction;
import com.paywallet.core.domain.model.User;
import com.paywallet.core.domain.model.Wallet;
import com.paywallet.core.domain.repository.TransactionRepository;
import com.paywallet.core.domain.repository.UserRepository;
import com.paywallet.core.domain.repository.WalletRepository;
import com.paywallet.core.infrastructure.messaging.KafkaProducerService;
import com.paywallet.core.infrastructure.messaging.event.TransactionInitiatedEvent;
import com.paywallet.core.infrastructure.service.IdempotencyService;
import com.paywallet.core.presentation.dto.TransferRequest;
import com.paywallet.core.domain.model.LedgerEntry;
import com.paywallet.core.domain.repository.LedgerRepository;
import com.paywallet.core.domain.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerRepository ledgerRepository;
    private final IdempotencyService idempotencyService;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public Transaction initiateTransfer(UUID senderId, TransferRequest request, String idempotencyKey) {
        // 1. Idempotency Check
        if (idempotencyKey != null && !idempotencyService.lock(idempotencyKey)) {
            // If locked, check if transaction exists
            return transactionRepository.findByIdempotencyKey(UUID.fromString(idempotencyKey))
                    .orElseThrow(() -> new IdempotencyException("Idempotency conflict for key: " + idempotencyKey));
        }

        // 2. Validate Amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // 3. Sender Wallet (Lock)
        Wallet senderWallet = walletRepository.findByUserIdWithLock(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));

        // 4. Check Balance
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in sender wallet");
        }

        // 5. Receiver Wallet Lookup
        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receiver not found with email: " + request.getReceiverEmail()));
        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver wallet not found"));

        if (senderWallet.getId().equals(receiverWallet.getId())) {
            throw new InvalidOperationException("Cannot transfer money to self");
        }

        // 6. Create Transaction (PENDING)
        Transaction transaction = Transaction.builder()
                .senderWalletId(senderWallet.getId())
                .receiverWalletId(receiverWallet.getId())
                .amount(request.getAmount())
                .status(Transaction.TransactionStatus.PENDING)
                .idempotencyKey(idempotencyKey != null ? UUID.fromString(idempotencyKey) : null)
                .build();

        transaction = transactionRepository.save(transaction);

        // 7. Publish Event
        TransactionInitiatedEvent event = TransactionInitiatedEvent.builder()
                .transactionId(transaction.getId())
                .senderWalletId(senderWallet.getId())
                .receiverWalletId(receiverWallet.getId())
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now().toString())
                .build();

        kafkaProducerService.publishTransactionInitiated(event);

        return transaction;
    }

    @Transactional
    public void completeTransfer(UUID transactionId, boolean approved, String reason) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));

        if (transaction.getStatus() != Transaction.TransactionStatus.PENDING) {
            return; // Already processed
        }

        if (!approved) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(reason);
            transactionRepository.save(transaction);
            return;
        }

        // Lock Wallets in Order to avoid Deadlock
        UUID senderId = transaction.getSenderWalletId();
        UUID receiverId = transaction.getReceiverWalletId();

        Wallet senderWallet;
        Wallet receiverWallet;

        if (senderId.compareTo(receiverId) < 0) {
            senderWallet = walletRepository.findByIdWithLock(senderId).orElseThrow();
            receiverWallet = walletRepository.findByIdWithLock(receiverId).orElseThrow();
        } else {
            receiverWallet = walletRepository.findByIdWithLock(receiverId).orElseThrow();
            senderWallet = walletRepository.findByIdWithLock(senderId).orElseThrow();
        }

        // Double check balance
        if (senderWallet.getBalance().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason("Insufficient funds during completion");
            transactionRepository.save(transaction);
            return;
        }

        // Perform Transfer
        senderWallet.setBalance(senderWallet.getBalance().subtract(transaction.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(transaction.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Create Ledger Entries
        LedgerEntry debitEntry = LedgerEntry.builder()
                .walletId(senderWallet.getId())
                .transactionId(transaction.getId())
                .type(LedgerEntry.LegacyType.DEBIT)
                .amount(transaction.getAmount())
                .description("Transfer to " + receiverWallet.getUserId()) // Should be User email ideally
                .balanceAfter(senderWallet.getBalance())
                .build();

        LedgerEntry creditEntry = LedgerEntry.builder()
                .walletId(receiverWallet.getId())
                .transactionId(transaction.getId())
                .type(LedgerEntry.LegacyType.CREDIT)
                .amount(transaction.getAmount())
                .description("Transfer from " + senderWallet.getUserId())
                .balanceAfter(receiverWallet.getBalance())
                .build();

        ledgerRepository.save(debitEntry);
        ledgerRepository.save(creditEntry);

        // Update Transaction
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
    }
}
