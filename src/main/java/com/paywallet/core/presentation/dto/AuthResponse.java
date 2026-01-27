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
@Schema(description = "Authentication response containing JWT tokens")
public class AuthResponse {
    @Schema(description = "JWT access token for API authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "JWT refresh token to obtain new access tokens", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
