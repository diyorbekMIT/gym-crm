package com.gym.crm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Trainee profile details")
public record TraineeProfileResponse(

        @Schema(
                description = "Trainee username",
                example = "john.smith"
        )
        String username,

        @Schema(
                description = "Trainee first name",
                example = "John"
        )
        String firstName,

        @Schema(
                description = "Trainee last name",
                example = "Smith"
        )
        String lastName,

        @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(
                description = "Trainee date of birth",
                example = "1995-05-10"
        )
        LocalDate dateOfBirth,

        @Schema(
                description = "Trainee address",
                example = "123 Main Street"
        )
        String address,

        @Schema(
                description = "Whether the trainee is active",
                example = "true"
        )
        boolean isActive,

        @Schema(description = "Trainers assigned to the trainee")
        List<TrainerSummaryResponse> trainers
) {
}