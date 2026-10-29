package com.gym.crm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary information about a trainee")
public record TraineeSummaryResponse(

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
        String lastName
) {
}