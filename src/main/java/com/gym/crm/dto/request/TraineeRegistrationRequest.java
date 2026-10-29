package com.gym.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

@Schema(description = "Request to register a trainee")
public record TraineeRegistrationRequest(

        @Schema(
                description = "Trainee first name",
                example = "John"
        )
        @NotBlank(message = "First name must not be blank.")
        String firstName,

        @Schema(
                description = "Trainee last name",
                example = "Smith"
        )
        @NotBlank(message = "Last name must not be blank.")
        String lastName,

        @Schema(
                description = "Trainee date of birth",
                example = "1995-05-10"
        )
        @Past(message = "Date of birth must be in the past.")
        LocalDate dateOfBirth,

        @Schema(
                description = "Trainee address",
                example = "123 Main Street"
        )
        String address
) {
}