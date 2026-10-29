package com.gym.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to authenticate a user")
public record LoginRequest(

        @Schema(
                description = "Username",
                example = "john.smith"
        )
        @NotBlank(message = "Username must not be blank.")
        String username,

        @Schema(
                description = "Password",
                example = "aB3dE5fG7h"
        )
        @NotBlank(message = "Password must not be blank.")
        String password
) {
}