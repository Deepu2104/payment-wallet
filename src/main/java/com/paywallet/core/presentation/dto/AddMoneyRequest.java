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
@Schema(description = "Request body to add money to wallet")
public class AddMoneyRequest {
    @Schema(description = "Amount to add", example = "100.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Description of the deposit", example = "Top up via Credit Card")
    private String description;
}
