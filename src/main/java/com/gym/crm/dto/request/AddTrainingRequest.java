package com.gym.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Request to add a training")
public record AddTrainingRequest(

        @Schema(
                description = "Trainee username",
                example = "john.smith"
        )
        @NotBlank(message = "Trainee username must not be blank.")
        String traineeUsername,

        @Schema(
                description = "Trainer username",
                example = "alice.brown"
        )
        @NotBlank(message = "Trainer username must not be blank.")
        String trainerUsername,

        @Schema(
                description = "Training name",
                example = "Morning strength session"
        )
        @NotBlank(message = "Training name must not be blank.")
        String trainingName,

        @Schema(
                description = "Training date",
                example = "2026-11-01"
        )
        @NotNull(message = "Training date must be provided.")
        LocalDate trainingDate,

        @Schema(
                description = "Training duration in minutes",
                example = "60"
        )
        @NotNull(message = "Training duration must be provided.")
        @Positive(message = "Training duration must be positive.")
        Integer trainingDuration,

        @Schema(
                description = "Training type ID",
                example = "1"
        )
        @NotNull(message = "Training type must be provided.")
        Long trainingTypeId
) {
}