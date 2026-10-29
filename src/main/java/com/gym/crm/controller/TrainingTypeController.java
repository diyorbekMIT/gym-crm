package com.gym.crm.controller;

import com.gym.crm.dto.response.TrainingTypeResponse;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.jpa.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Training Types",
        description = "Read-only training type reference data"
)
@RestController
@RequestMapping("/api/v1/training-types")
public class TrainingTypeController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainingTypeController.class);

    private final TrainingTypeService trainingTypeService;
    private final TrainingMapper trainingMapper;

    public TrainingTypeController(
            TrainingTypeService trainingTypeService,
            TrainingMapper trainingMapper
    ) {
        this.trainingTypeService = trainingTypeService;
        this.trainingMapper = trainingMapper;
    }

    @Operation(
            summary = "Get all training types",
            description = "Returns the fixed list of available training types."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Training types returned successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        LOGGER.info("Fetching all training types.");

        List<TrainingTypeResponse> response = trainingTypeService
                .getAll()
                .stream()
                .map(trainingMapper::toTrainingTypeResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}