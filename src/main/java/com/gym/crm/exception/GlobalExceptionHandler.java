package com.gym.crm.exception;

import com.gym.crm.service.exception.AccessDeniedException;
import com.gym.crm.service.exception.AuthenticationException;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            EntityNotFoundException exception
    ) {
        LOGGER.warn("Resource not found: {}", exception.getMessage());

        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            AuthenticationException exception
    ) {
        LOGGER.warn("Authentication failed: {}", exception.getMessage());

        return build(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception
    ) {
        LOGGER.warn("Access denied: {}", exception.getMessage());

        return build(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            RuntimeException exception
    ) {
        LOGGER.warn("Bad request: {}", exception.getMessage());

        return build(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        LOGGER.warn("Request validation failed: {}", errors);

        return build(
                HttpStatus.BAD_REQUEST,
                "Request validation failed.",
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception
    ) {
        LOGGER.error("Unexpected API error.", exception);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred."
        );
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String message
    ) {
        return build(status, message, null);
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String message,
            Map<String, String> details
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );

        return ResponseEntity.status(status).body(response);
    }


}