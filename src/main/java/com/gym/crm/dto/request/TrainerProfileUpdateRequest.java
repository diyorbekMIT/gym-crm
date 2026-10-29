package com.gym.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update a trainer profile")
public record TrainerProfileUpdateRequest(

        @Schema(
                description = "Trainer first name",
                example = "Alice"
        )
        @NotBlank(message = "First name must not be blank.")
        String firstName,

        @Schema(
                description = "Trainer last name",
                example = "Brown"
        )
        @NotBlank(message = "Last name must not be blank.")
        String lastName,

        @Schema(
                description = "Whether the trainer is active",
                example = "true"
        )
        @NotNull(message = "Active status must be provided.")
        Boolean isActive
) {
}