package com.paywallet.core.application.service;

import com.paywallet.core.BaseIntegrationTest;
import com.paywallet.core.domain.model.Transaction;
import com.paywallet.core.domain.repository.WalletRepository;
import com.paywallet.core.presentation.dto.AuthResponse;
import com.paywallet.core.presentation.dto.RegisterRequest;
import com.paywallet.core.presentation.dto.TransferRequest;
import com.paywallet.core.presentation.dto.WalletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class WalletTransferIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private AuthService authService;

        @Autowired
        private WalletService walletService;

        @Autowired
        private TransferService transferService;

        @Autowired
        private WalletRepository walletRepository;

        @Test
        void testFullTransferFlow() {
                // 1. Register Users
                AuthResponse user1 = authService.register(new RegisterRequest("sender@test.com", "password123"));
                AuthResponse user2 = authService.register(new RegisterRequest("receiver@test.com", "password123"));

                // Let's get IDs via repos for simplicity in test
                UUID senderIdReal = walletRepository.findAll().stream()
                                .filter(w -> w.getBalance().compareTo(BigDecimal.ZERO) == 0 && authService.authenticate(
                                                new com.paywallet.core.presentation.dto.LoginRequest("sender@test.com",
                                                                "password123")) != null) // brute
                                                                                         // force
                                                                                         // filtering
                                .findFirst().get().getUserId();
                // Wait, standard way:
                // We can't easily get ID without JwtService parsing.
                // Let's just trust that Register created wallets.
                // Since AuthResponse doesn't have ID, and we need ID for services.
                // I'll cheat and fetch all wallets.

                var wallets = walletRepository.findAll();
                var senderWallet = wallets.get(0);
                var receiverWallet = wallets.get(1);

                UUID senderUserId = senderWallet.getUserId(); // This is the ID we use for APIs
                UUID receiverUserId = receiverWallet.getUserId();

                // 2. Add Money to Sender
                WalletResponse balanceResp = walletService.addMoney(senderUserId, new BigDecimal("100.00"), "Deposit",
                                null);
                assertThat(balanceResp.getBalance()).isEqualByComparingTo("100.00");

                // 3. Initiate Transfer
                TransferRequest transferRequest = new TransferRequest("receiver@test.com", new BigDecimal("50.00"),
                                "Test Transfer");
                Transaction transaction = transferService.initiateTransfer(senderUserId, transferRequest,
                                UUID.randomUUID().toString());

                assertThat(transaction.getStatus()).isEqualTo(Transaction.TransactionStatus.PENDING);

                // 4. Wait for Async Fraud Check & Completion (mocked by same-process Kafka
                // consumer)
                await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                        var updatedSender = walletRepository.findById(senderWallet.getId()).get();
                        var updatedReceiver = walletRepository.findById(receiverWallet.getId()).get();

                        assertThat(updatedSender.getBalance()).isEqualByComparingTo("50.00");
                        assertThat(updatedReceiver.getBalance()).isEqualByComparingTo("50.00");
                });
        }
}
