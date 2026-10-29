package com.gym.crm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credentials generated during profile registration")
public record RegistrationResponse(

        @Schema(
                description = "Generated username",
                example = "john.smith"
        )
        String username,

        @Schema(
                description = "Generated password",
                example = "aB3dE5fG7h"
        )
        String password
) {
}