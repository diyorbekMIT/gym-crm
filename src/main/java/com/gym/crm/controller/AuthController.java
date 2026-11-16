package com.gym.crm.controller;

import com.gym.crm.dto.request.LoginRequest;
import com.gym.crm.dto.response.LoginResponse;
import com.gym.crm.exception.ApiErrorResponse;
import com.gym.crm.security.BruteForceProtectionService;
import com.gym.crm.security.JwtService;
import com.gym.crm.security.TokenBlacklistService;
import com.gym.crm.service.exception.AuthenticationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Tag(name = "Authentication", description = "User login and logout operations")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BruteForceProtectionService bruteForceService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            BruteForceProtectionService bruteForceService,
            TokenBlacklistService tokenBlacklistService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.bruteForceService = bruteForceService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Operation(summary = "User Login", description = "Authenticates user and returns JWT Bearer token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "User blocked due to 3 failed attempts")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // 1. Check if user is currently blocked by brute force protector
        if (bruteForceService.isBlocked(request.username())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiErrorResponse(
                            Instant.now(),
                            HttpStatus.FORBIDDEN.value(),
                            "Forbidden",
                            "User is temporarily blocked for 5 minutes due to 3 consecutive failed login attempts.",
                            null
                    ));
        }

        try {
            // 2. Delegate authentication to Spring Security (checks BCrypt password)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException ex) {
            // 3. Failed login: increment failed counter
            bruteForceService.loginFailed(request.username());

            if (bruteForceService.isBlocked(request.username())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiErrorResponse(
                                Instant.now(),
                                HttpStatus.FORBIDDEN.value(),
                                "Forbidden",
                                "User is blocked for 5 minutes due to 3 consecutive failed login attempts.",
                                null
                        ));
            }

            throw new AuthenticationException();
        }

        // 4. Successful login: reset failed counter and generate JWT
        bruteForceService.loginSucceeded(request.username());
        String token = jwtService.generateToken(request.username());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @Operation(summary = "User Logout", description = "Invalidates the current JWT token")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        return ResponseEntity.ok().build();
    }
}