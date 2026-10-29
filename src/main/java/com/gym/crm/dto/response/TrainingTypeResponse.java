package com.gym.crm.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Training type reference data")
public record TrainingTypeResponse(

        @Schema(
                description = "Training type ID",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Training type name",
                example = "STRENGTH"
        )
        String trainingTypeName
) {
}