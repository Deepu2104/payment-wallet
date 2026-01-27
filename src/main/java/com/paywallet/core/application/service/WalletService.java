package com.paywallet.core.application.service;

import com.paywallet.core.domain.model.LedgerEntry;
import com.paywallet.core.domain.model.Wallet;
import com.paywallet.core.domain.repository.LedgerRepository;
import com.paywallet.core.domain.repository.WalletRepository;
import com.paywallet.core.presentation.dto.LedgerEntryResponse;
import com.paywallet.core.presentation.dto.WalletResponse;
import com.paywallet.core.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

        private final WalletRepository walletRepository;
        private final LedgerRepository ledgerRepository;

        @Transactional(readOnly = true)
        public WalletResponse getBalance(UUID userId) {
                Wallet wallet = walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Wallet not found for user: " + userId));

                return WalletResponse.builder()
                                .walletId(wallet.getId())
                                .balance(wallet.getBalance())
                                .currency(wallet.getCurrency())
                                .build();
        }

        @Transactional
        public WalletResponse addMoney(UUID userId, BigDecimal amount, String description, UUID transactionId) {
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                }

                // 1. Lock Wallet
                Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Wallet not found for user: " + userId));

                // 2. Update Balance
                BigDecimal newBalance = wallet.getBalance().add(amount);
                wallet.setBalance(newBalance);
                walletRepository.save(wallet);

                // 3. Create Ledger Entry
                LedgerEntry entry = LedgerEntry.builder()
                                .walletId(wallet.getId())
                                .transactionId(transactionId != null ? transactionId : UUID.randomUUID())
                                .type(LedgerEntry.LegacyType.CREDIT)
                                .amount(amount)
                                .description(description != null ? description : "Add Money")
                                .balanceAfter(newBalance)
                                .build();

                ledgerRepository.save(entry);

                return WalletResponse.builder()
                                .walletId(wallet.getId())
                                .balance(newBalance)
                                .currency(wallet.getCurrency())
                                .build();
        }

        @Transactional(readOnly = true)
        public java.util.List<LedgerEntryResponse> getStatement(UUID userId) {
                Wallet wallet = walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Wallet not found for user: " + userId));

                return ledgerRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId()).stream()
                                .map(entry -> LedgerEntryResponse.builder()
                                                .id(entry.getId())
                                                .transactionId(entry.getTransactionId())
                                                .type(entry.getType().name())
                                                .amount(entry.getAmount())
                                                .description(entry.getDescription())
                                                .balanceAfter(entry.getBalanceAfter())
                                                .createdAt(entry.getCreatedAt())
                                                .build())
                                .toList();
        }
}
