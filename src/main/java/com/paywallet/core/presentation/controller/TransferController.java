package com.paywallet.core.presentation.controller;

import com.paywallet.core.application.service.TransferService;
import com.paywallet.core.domain.model.Transaction;
import com.paywallet.core.domain.model.User;
import com.paywallet.core.presentation.dto.TransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for initiating value transfers")
public class TransferController {

    private final TransferService transferService;
    private final com.paywallet.core.domain.repository.UserRepository userRepository;

    private UUID getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Initiate a money transfer", description = "Initiates a transfer between users. The transfer is processed asynchronously after fraud checks.")
    public ResponseEntity<Transaction> initiateTransfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TransferRequest request,
            @Parameter(description = "Unique ID for idempotency", example = "550e8400-e29b-41d4-a716-446655440000") @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return ResponseEntity.ok(transferService.initiateTransfer(
                getUserId(userDetails),
                request,
                idempotencyKey));
    }
}
