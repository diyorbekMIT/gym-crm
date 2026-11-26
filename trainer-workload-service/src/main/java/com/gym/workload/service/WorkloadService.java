package com.gym.workload.service;

import com.gym.workload.dto.request.TrainerWorkloadRequest;
import com.gym.workload.dto.response.TrainerWorkloadSummaryResponse;
import com.gym.workload.model.TrainerWorkloadEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class WorkloadService {

    // key = trainerUsername, value = their workload entry
    private final Map<String, TrainerWorkloadEntry> storage = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadService.class);

    public void processWorkload(TrainerWorkloadRequest request) {
        TrainerWorkloadEntry entry = storage.computeIfAbsent(
                request.trainerUsername(),
                username -> new TrainerWorkloadEntry(
                        username,
                        request.trainerFirstName(),
                        request.trainerLastName(),
                        request.isActive()
                )
        );

        int year  = request.trainingDate().getYear();
        int month = request.trainingDate().getMonthValue();
        int duration = request.trainingDuration();

        if (request.actionType() == TrainerWorkloadRequest.ActionType.ADD) {
            entry.addMinutes(year, month, duration);
        } else {
            entry.subtractMinutes(year, month, duration);
        }

        LOGGER.info(
                "Operation: processWorkload | transactionId={} | trainer={} | action={} | year={} | month={} | duration={}",
                MDC.get("transactionId"),
                request.trainerUsername(),
                request.actionType(),
                year,
                month,
                duration
        );
    }

    public TrainerWorkloadSummaryResponse getTrainerSummary(String username) {
        TrainerWorkloadEntry entry = storage.get(username);
        if (entry == null) {
            return null;
        }

        List<TrainerWorkloadSummaryResponse.YearSummary> years = entry.getYearlySummary()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(yearEntry -> new TrainerWorkloadSummaryResponse.YearSummary(
                        yearEntry.getKey(),
                        yearEntry.getValue().entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(m -> new TrainerWorkloadSummaryResponse.MonthSummary(
                                        m.getKey(), m.getValue()))
                                .toList()
                ))
                .toList();

        LOGGER.info(
                "Operation: getTrainerSummary | transactionId={} | trainer={} | yearsCount={}",
                MDC.get("transactionId"),
                username,
                years.size()
        );

        return new TrainerWorkloadSummaryResponse(
                entry.getUsername(),
                entry.getFirstName(),
                entry.getLastName(),
                entry.isActive(),
                years
        );
    }

    public Map<String, TrainerWorkloadEntry> getAll() {
        return storage;
    }
}