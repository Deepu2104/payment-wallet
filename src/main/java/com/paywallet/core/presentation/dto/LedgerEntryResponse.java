package com.paywallet.core.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Detailed ledger entry for transaction history")
public class LedgerEntryResponse {
    @Schema(description = "Unique ID of the ledger entry")
    private UUID id;

    @Schema(description = "Associated transaction ID")
    private UUID transactionId;

    @Schema(description = "Type of entry (DEBIT/CREDIT)", example = "CREDIT")
    private String type;

    @Schema(description = "Transaction amount", example = "50.00")
    private BigDecimal amount;

    @Schema(description = "Transaction description", example = "Transfer from user B")
    private String description;

    @Schema(description = "Wallet balance after this transaction", example = "550.25")
    private BigDecimal balanceAfter;

    @Schema(description = "Timestamp of the transaction")
    private LocalDateTime createdAt;
}
