package com.gym.crm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GymMetricsService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GymMetricsService.class);

    private final Counter traineeRegistrationCounter;
    private final Counter trainerRegistrationCounter;
    private final Counter trainingCreationCounter;

    public GymMetricsService(MeterRegistry meterRegistry) {
        this.traineeRegistrationCounter = Counter.builder(
                        "gym_trainee_registrations_total"
                )
                .description("Total number of trainee registrations")
                .register(meterRegistry);

        this.trainerRegistrationCounter = Counter.builder(
                        "gym_trainer_registrations_total"
                )
                .description("Total number of trainer registrations")
                .register(meterRegistry);

        this.trainingCreationCounter = Counter.builder(
                        "gym_trainings_created_total"
                )
                .description("Total number of created trainings")
                .register(meterRegistry);

        LOGGER.info("Custom Gym CRM metrics initialized.");
    }

    public void incrementTraineeRegistrations() {
        traineeRegistrationCounter.increment();
    }

    public void incrementTrainerRegistrations() {
        trainerRegistrationCounter.increment();
    }

    public void incrementTrainingCreations() {
        trainingCreationCounter.increment();
    }
}