package com.gym.crm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary information about a trainer")
public record TrainerSummaryResponse(

        @Schema(
                description = "Trainer username",
                example = "alice.brown"
        )
        String username,

        @Schema(
                description = "Trainer first name",
                example = "Alice"
        )
        String firstName,

        @Schema(
                description = "Trainer last name",
                example = "Brown"
        )
        String lastName,

        @Schema(
                description = "Training type ID",
                example = "1"
        )
        Long specializationId,

        @Schema(
                description = "Training type name",
                example = "STRENGTH"
        )
        String specializationName
) {
}