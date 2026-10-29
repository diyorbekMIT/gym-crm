package com.gym.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to activate or deactivate a trainer")
public record TrainerStatusRequest(

        @Schema(
                description = "Whether the trainer should be active",
                example = "true"
        )
        @NotNull(message = "Active status must be provided.")
        Boolean isActive
) {
}