package com.paywallet.core.presentation.controller;

import com.paywallet.core.application.service.WalletService;
import com.paywallet.core.domain.model.User;
import com.paywallet.core.presentation.dto.AddMoneyRequest;
import com.paywallet.core.presentation.dto.LedgerEntryResponse;
import com.paywallet.core.presentation.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Endpoints for balance, depositing money, and transaction history")
public class WalletController {

    private final WalletService walletService;
    private final com.paywallet.core.domain.repository.UserRepository userRepository; // Direct repo access for ID
                                                                                      // lookup - simpler for now

    // Helper to get User ID from details
    private UUID getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/balance")
    @Operation(summary = "Get current wallet balance", description = "Returns the balance and currency of the authenticated user's wallet")
    public ResponseEntity<WalletResponse> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.getBalance(getUserId(userDetails)));
    }

    @PostMapping("/add-money")
    @Operation(summary = "Deposit money to wallet", description = "Adds funds to the authenticated user's wallet and records a ledger entry")
    public ResponseEntity<WalletResponse> addMoney(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddMoneyRequest request) {
        return ResponseEntity.ok(walletService.addMoney(
                getUserId(userDetails),
                request.getAmount(),
                request.getDescription(),
                UUID.randomUUID() // Generating txn Id here for now
        ));
    }

    @GetMapping("/statement")
    @Operation(summary = "Get transaction history", description = "Returns a list of all ledger entries (DEBIT/CREDIT) for the authenticated user")
    public ResponseEntity<java.util.List<LedgerEntryResponse>> getStatement(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.getStatement(getUserId(userDetails)));
    }
}
