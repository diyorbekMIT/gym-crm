package com.gym.crm.controller;

import com.gym.crm.dto.request.AddTrainingRequest;
import com.gym.crm.dto.response.TrainingResponse;
import com.gym.crm.entity.Training;
import com.gym.crm.facade.JpaGymFacade;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.exception.AccessDeniedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "Trainings",
        description = "Training creation and filtered training searches"
)
@RestController
@RequestMapping("/api/v1")
public class TrainingController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainingController.class);

    private final JpaGymFacade facade;
    private final TrainingMapper trainingMapper;

    public TrainingController(
            JpaGymFacade facade,
            TrainingMapper trainingMapper
    ) {
        this.facade = facade;
        this.trainingMapper = trainingMapper;
    }

    @Operation(
            summary = "Add a training",
            description = """
                    Creates a training for the authenticated trainee and
                    the specified trainer.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Trainee, trainer, or training type not found")
    })
    @PostMapping("/trainings")
    public ResponseEntity<Void> addTraining(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @Valid @RequestBody AddTrainingRequest request
    ) {
        requireOwnProfile(authUsername, request.traineeUsername());

        facade.addTrainingByUsernames(
                authUsername,
                authPassword,
                request.traineeUsername(),
                request.trainerUsername(),
                request.trainingName(),
                request.trainingTypeId(),
                request.trainingDate(),
                request.trainingDuration()
        );

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get trainee trainings",
            description = "Returns the authenticated trainee's trainings using optional filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/trainees/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainings(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) Long trainingTypeId
    ) {
        requireOwnProfile(authUsername, username);

        List<Training> trainings = facade.getTraineeTrainings(
                username,
                authPassword,
                fromDate,
                toDate,
                trainerName,
                trainingTypeId
        );

        return ResponseEntity.ok(
                trainingMapper.toTraineeTrainingResponses(trainings)
        );
    }

    @Operation(
            summary = "Get trainer trainings",
            description = "Returns the authenticated trainer's trainings using optional filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings returned"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/trainers/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @RequestHeader(AuthHeaders.USERNAME) String authUsername,
            @RequestHeader(AuthHeaders.PASSWORD) String authPassword,
            @PathVariable("username") String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false) String traineeName
    ) {
        requireOwnProfile(authUsername, username);

        List<Training> trainings = facade.getTrainerTrainings(
                username,
                authPassword,
                fromDate,
                toDate,
                traineeName
        );

        return ResponseEntity.ok(
                trainingMapper.toTrainerTrainingResponses(trainings)
        );
    }

    private void requireOwnProfile(
            String authenticatedUsername,
            String requestedUsername
    ) {
        if (!authenticatedUsername.equalsIgnoreCase(requestedUsername)) {
            throw new AccessDeniedException(
                    "You can access only your own training data."
            );
        }
    }
}