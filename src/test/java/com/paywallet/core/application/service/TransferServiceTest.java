package com.paywallet.core.application.service;

import com.paywallet.core.domain.model.Transaction;
import com.paywallet.core.domain.model.User;
import com.paywallet.core.domain.model.Wallet;
import com.paywallet.core.domain.repository.LedgerRepository;
import com.paywallet.core.domain.repository.TransactionRepository;
import com.paywallet.core.domain.repository.UserRepository;
import com.paywallet.core.domain.repository.WalletRepository;
import com.paywallet.core.infrastructure.messaging.KafkaProducerService;
import com.paywallet.core.infrastructure.service.IdempotencyService;
import com.paywallet.core.presentation.dto.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private LedgerRepository ledgerRepository;
    @Mock
    private IdempotencyService idempotencyService;
    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private TransferService transferService;

    private UUID senderId;
    private UUID receiverId;
    private Wallet senderWallet;
    private Wallet receiverWallet;
    private User receiverUser;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();

        senderWallet = Wallet.builder()
                .userId(senderId)
                .balance(new BigDecimal("100.00"))
                .currency("USD")
                .build();
        senderWallet.setId(UUID.randomUUID());

        receiverWallet = Wallet.builder()
                .userId(receiverId)
                .balance(new BigDecimal("50.00"))
                .currency("USD")
                .build();
        receiverWallet.setId(UUID.randomUUID());

        receiverUser = User.builder()
                .email("receiver@test.com")
                .build();
        receiverUser.setId(receiverId);
    }

    @Test
    void initiateTransfer_Success() {
        // Arrange
        TransferRequest request = new TransferRequest("receiver@test.com", new BigDecimal("50.00"), "Test");
        String idempotencyKey = UUID.randomUUID().toString();

        when(idempotencyService.lock(idempotencyKey)).thenReturn(true);
        when(walletRepository.findByUserIdWithLock(senderId)).thenReturn(Optional.of(senderWallet));
        when(userRepository.findByEmail(request.getReceiverEmail())).thenReturn(Optional.of(receiverUser));
        when(walletRepository.findByUserId(receiverId)).thenReturn(Optional.of(receiverWallet));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        // Act
        Transaction result = transferService.initiateTransfer(senderId, request, idempotencyKey);

        // Assert
        assertNotNull(result);
        assertEquals(Transaction.TransactionStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("50.00"), result.getAmount());

        verify(kafkaProducerService).publishTransactionInitiated(any());
    }

    @Test
    void initiateTransfer_InsufficientBalance() {
        // Arrange
        TransferRequest request = new TransferRequest("receiver@test.com", new BigDecimal("200.00"), "Test");

        when(walletRepository.findByUserIdWithLock(senderId)).thenReturn(Optional.of(senderWallet));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> transferService.initiateTransfer(senderId, request, null));
        verify(kafkaProducerService, never()).publishTransactionInitiated(any());
    }
}
