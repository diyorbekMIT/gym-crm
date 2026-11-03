package com.gym.crm.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GymMetricsServiceTest {

    @Test
    void shouldIncrementCustomMetrics() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        GymMetricsService metricsService =
                new GymMetricsService(registry);

        metricsService.incrementTraineeRegistrations();
        metricsService.incrementTraineeRegistrations();
        metricsService.incrementTrainerRegistrations();
        metricsService.incrementTrainingCreations();

        assertThat(registry.counter(
                "gym_trainee_registrations_total"
        ).count()).isEqualTo(2.0);

        assertThat(registry.counter(
                "gym_trainer_registrations_total"
        ).count()).isEqualTo(1.0);

        assertThat(registry.counter(
                "gym_trainings_created_total"
        ).count()).isEqualTo(1.0);
    }
}