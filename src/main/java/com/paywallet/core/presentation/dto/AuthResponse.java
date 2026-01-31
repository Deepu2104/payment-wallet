package com.paywallet.core.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication response containing session token")
public class AuthResponse {
    @Schema(description = "Opaque session token for API authorization", example = "550e8400-e29b-41d4-a716-446655440000")
    private String accessToken;
}
