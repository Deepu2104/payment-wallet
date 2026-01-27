package com.paywallet.core.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body to initiate a money transfer between users")
public class TransferRequest {
    @Schema(description = "Email of the recipient user", example = "receiver@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiverEmail;

    @Schema(description = "Amount to transfer", example = "50.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Optional note or reason for the transfer", example = "Dinner repayment")
    private String note;
}
