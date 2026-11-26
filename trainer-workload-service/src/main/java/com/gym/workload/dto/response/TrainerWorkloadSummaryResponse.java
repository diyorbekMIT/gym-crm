package com.gym.workload.dto.response;

import java.util.List;

public record TrainerWorkloadSummaryResponse(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        boolean trainerStatus,
        List<YearSummary> years
) {
    public record YearSummary(int year, List<MonthSummary> months) {}
    public record MonthSummary(int month, int trainingSummaryDuration) {}
}
