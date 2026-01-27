package com.paywallet.core.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Wallet balance information")
public class WalletResponse {
    @Schema(description = "Unique ID of the wallet")
    private UUID walletId;

    @Schema(description = "Current available balance", example = "500.25")
    private BigDecimal balance;

    @Schema(description = "Wallet currency", example = "USD")
    private String currency;
}
