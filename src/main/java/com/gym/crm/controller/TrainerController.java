package com.gym.crm.controller;

import com.gym.crm.dto.request.PasswordChangeRequest;
import com.gym.crm.dto.request.TrainerProfileUpdateRequest;
import com.gym.crm.dto.request.TrainerRegistrationRequest;
import com.gym.crm.dto.request.TrainerStatusRequest;
import com.gym.crm.dto.response.RegistrationResponse;
import com.gym.crm.dto.response.TrainerProfileResponse;
import com.gym.crm.entity.Trainer;
import com.gym.crm.facade.JpaGymFacade;
import com.gym.crm.mapper.ProfileMapper;
import com.gym.crm.service.exception.AccessDeniedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Trainers",
        description = "Trainer registration, profile, and status operations"
)
@RestController
@RequestMapping("/api/v1/trainers")
public class TrainerController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainerController.class);

    private final JpaGymFacade facade;
    private final ProfileMapper profileMapper;

    public TrainerController(
            JpaGymFacade facade,
            ProfileMapper profileMapper
    ) {
        this.facade = facade;
        this.profileMapper = profileMapper;
    }

    @Operation(
            summary = "Register a trainer",
            description = """
                    Creates a trainer profile and returns the generated
                    username and password.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Training type not found")
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest request
    ) {
        LOGGER.info(
                "Registering trainer firstName={} lastName={}.",
                request.firstName(),
                request.lastName()
        );

        Trainer trainer = facade.createTrainer(
                request.firstName(),
                request.lastName(),
                request.specializationId()
        );

        RegistrationResponse response = new RegistrationResponse(
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Get trainer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username
    ) {
        Trainer trainer = facade.getTrainerProfileByUsername(
                authUsername,
                authPassword,
                username
        );

        return ResponseEntity.ok(
                profileMapper.toTrainerProfileResponse(trainer)
        );
    }

    @Operation(
            summary = "Update trainer profile",
            description = """
                    Updates trainer name and active status.
                    Specialization is read-only and cannot be changed.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @Valid @RequestBody TrainerProfileUpdateRequest request
    ) {
        requireOwnProfile(authUsername, username);

        Trainer trainer = facade.updateTrainerProfile(
                username,
                authPassword,
                request.firstName(),
                request.lastName(),
                request.isActive()
        );

        return ResponseEntity.ok(
                profileMapper.toTrainerProfileResponse(trainer)
        );
    }

    @Operation(summary = "Change trainer password")
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        requireOwnProfile(authUsername, username);

        facade.changeTrainerPassword(
                username,
                request.currentPassword(),
                request.newPassword()
        );

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Activate or deactivate trainer",
            description = "Activation and deactivation are not idempotent operations."
    )
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> changeStatus(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @Valid @RequestBody TrainerStatusRequest request
    ) {
        requireOwnProfile(authUsername, username);

        if (Boolean.TRUE.equals(request.isActive())) {
            facade.activateTrainer(username, authPassword);
        } else {
            facade.deactivateTrainer(username, authPassword);
        }

        return ResponseEntity.ok().build();
    }

    private void requireOwnProfile(
            String authenticatedUsername,
            String requestedUsername
    ) {
        if (!authenticatedUsername.equalsIgnoreCase(requestedUsername)) {
            throw new AccessDeniedException(
                    "You can access only your own profile."
            );
        }
    }
}