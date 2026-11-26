package com.gym.workload.controller;

import com.gym.workload.dto.request.TrainerWorkloadRequest;
import com.gym.workload.dto.response.TrainerWorkloadSummaryResponse;
import com.gym.workload.service.WorkloadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainer-workload")
public class WorkloadController {

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    // POST /api/v1/trainer-workload
    // Called by gym-crm every time a training is added or deleted
    @PostMapping
    public ResponseEntity<Void> updateWorkload(
            @Valid @RequestBody TrainerWorkloadRequest request
    ) {
        workloadService.processWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadSummaryResponse> getTrainerWorkload(
            @PathVariable String username
    ) {
        TrainerWorkloadSummaryResponse summary = workloadService.getTrainerSummary(username);
        if (summary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(summary);
    }
}