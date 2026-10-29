package com.gym.crm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Training details")
public record TrainingResponse(

        @Schema(
                description = "Training name",
                example = "Morning strength session"
        )
        String trainingName,

        @Schema(
                description = "Training date",
                example = "2026-11-01"
        )
        LocalDate trainingDate,

        @Schema(
                description = "Training type name",
                example = "STRENGTH"
        )
        String trainingTypeName,

        @Schema(
                description = "Training duration in minutes",
                example = "60"
        )
        Integer trainingDuration,

        @Schema(
                description = "Counterparty name: trainer name for trainee trainings, trainee name for trainer trainings",
                example = "Alice Brown"
        )
        String counterpartyName
) {
}