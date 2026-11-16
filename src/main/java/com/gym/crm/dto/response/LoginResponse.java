package com.gym.crm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing JWT token")
public record LoginResponse(

        @Schema(description = "JWT Bearer token")
        String token,

        @Schema(description = "Token type", example = "Bearer")
        String tokenType
) {
    public LoginResponse(String token) {
        this(token, "Bearer");
    }
}