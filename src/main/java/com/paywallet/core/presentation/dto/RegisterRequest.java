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
    @Schema(description = "User full name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "User email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User password", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    @jakarta.validation.constraints.NotBlank(message = "Password is required")
    private String password;
}
