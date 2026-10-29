package com.gym.crm.controller;

import com.gym.crm.dto.request.TraineeRegistrationRequest;
import com.gym.crm.dto.response.RegistrationResponse;
import com.gym.crm.dto.response.TraineeProfileResponse;
import com.gym.crm.entity.Trainee;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gym.crm.dto.request.PasswordChangeRequest;
import com.gym.crm.dto.request.TraineeProfileUpdateRequest;
import com.gym.crm.dto.request.TraineeStatusRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.gym.crm.dto.request.TraineeTrainersUpdateRequest;
import com.gym.crm.dto.response.TrainerSummaryResponse;
import com.gym.crm.entity.Trainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

@Tag(
        name = "Trainees",
        description = "Trainee registration and profile operations"
)
@RestController
@RequestMapping("/api/v1/trainees")
public class TraineeController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TraineeController.class);

    private final JpaGymFacade facade;
    private final ProfileMapper profileMapper;

    public TraineeController(JpaGymFacade facade, ProfileMapper profileMapper) {
        this.facade = facade;
        this.profileMapper = profileMapper;
    }

    @Operation(
            summary = "Get trainee profile",
            description = "Returns the authenticated trainee's profile and assigned trainers."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username
    ) {
        Trainee trainee = facade.getTraineeProfileByUsername(
                authUsername,
                authPassword,
                username
        );

        return ResponseEntity.ok(
                profileMapper.toTraineeProfileResponse(trainee)
        );
    }

    @Operation(
            summary = "Update trainee profile",
            description = "Updates the authenticated trainee's profile and active status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> updateTraineeProfile(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @Valid @RequestBody TraineeProfileUpdateRequest request
    ) {
        requireOwnProfile(authUsername, username);

        Trainee trainee = facade.updateTraineeProfile(
                username,
                authPassword,
                request.firstName(),
                request.lastName(),
                request.dateOfBirth(),
                request.address(),
                request.isActive()
        );

        return ResponseEntity.ok(
                profileMapper.toTraineeProfileResponse(trainee)
        );
    }
    @Operation(
            summary = "Get active trainers not assigned to the trainee",
            description = "Returns active trainers who are not currently assigned to the authenticated trainee."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> getUnassignedTrainers(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username
    ) {
        requireOwnProfile(authUsername, username);

        List<Trainer> trainers = facade.getUnassignedTrainersByUsername(
                username,
                authPassword
        );

        return ResponseEntity.ok(
                profileMapper.toTrainerSummaries(trainers)
        );
    }

    @Operation(
            summary = "Replace trainee's assigned trainers",
            description = "Replaces the trainee's current trainer list with the provided trainer usernames."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> replaceTrainers(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @Valid @RequestBody TraineeTrainersUpdateRequest request
    ) {
        requireOwnProfile(authUsername, username);

        List<Trainer> trainers =
                facade.replaceAssignedTrainersByUsernames(
                        username,
                        authPassword,
                        request.trainerUsernames()
                );

        return ResponseEntity.ok(
                profileMapper.toTrainerSummaries(trainers)
        );
    }


    @Operation(summary = "Change trainee password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        requireOwnProfile(authUsername, username);

        facade.changeTraineePassword(
                username,
                request.currentPassword(),
                request.newPassword()
        );

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Activate or deactivate trainee",
            description = "Activation and deactivation are not idempotent operations."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status changed"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> changeStatus(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @Valid @RequestBody TraineeStatusRequest request
    ) {
        requireOwnProfile(authUsername, username);

        if (Boolean.TRUE.equals(request.isActive())) {
            facade.activateTrainee(username, authPassword);
        } else {
            facade.deactivateTrainee(username, authPassword);
        }

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Register a trainee",
            description = """
                    Creates a trainee profile and returns the generated
                    username and password.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Trainee registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest request
    ) {
        LOGGER.info(
                "Registering trainee firstName={} lastName={}.",
                request.firstName(),
                request.lastName()
        );

        Trainee trainee = facade.createTrainee(
                request.firstName(),
                request.lastName(),
                request.dateOfBirth(),
                request.address()
        );

        RegistrationResponse response = new RegistrationResponse(
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword()
        );

        LOGGER.info(
                "Trainee registered successfully username={}.",
                response.username()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Delete trainee profile",
            description = """
                Permanently deletes the authenticated trainee and
                cascades deletion of related trainings.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username
    ) {
        requireOwnProfile(authUsername, username);

        facade.deleteTrainee(username, authPassword);

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