package com.gym.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to change a profile password")
public record PasswordChangeRequest(

        @Schema(
                description = "Current password",
                example = "aB3dE5fG7h"
        )
        @NotBlank(message = "Current password must not be blank.")
        String currentPassword,

        @Schema(
                description = "New password",
                example = "nEw9pAsSwOrD"
        )
        @NotBlank(message = "New password must not be blank.")
        String newPassword
) {
}