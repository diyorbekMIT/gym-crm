package com.gym.crm.dto.request;

import java.time.LocalDate;

public record TrainerWorkloadRequest(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean isActive,
        LocalDate trainingDate,
        int trainingDuration,
        ActionType actionType
) {
    public enum ActionType {
        ADD, DELETE
    }
}