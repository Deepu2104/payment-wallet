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
@Schema(description = "User registration request body")
public class RegisterRequest {
    @Schema(description = "User email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "User password", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
