package com.gym.crm.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard API error response")
public record ApiErrorResponse(

        @Schema(description = "Time when the error occurred")
        Instant timestamp,

        @Schema(
                description = "HTTP status code",
                example = "400"
        )
        int status,

        @Schema(
                description = "HTTP status reason phrase",
                example = "Bad Request"
        )
        String error,

        @Schema(
                description = "Error message",
                example = "Request validation failed."
        )
        String message,

        @Schema(description = "Field-level validation errors")
        Map<String, String> details
) {
}