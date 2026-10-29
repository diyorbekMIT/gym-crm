package com.gym.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request to replace a trainee's assigned trainers")
public record TraineeTrainersUpdateRequest(

        @Schema(
                description = "Usernames of trainers to assign",
                example = "[\"alice.brown\", \"bob.jones\"]"
        )
        @NotEmpty(message = "Trainer username list must not be empty.")
        List<String> trainerUsernames
) {
}