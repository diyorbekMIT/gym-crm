package com.gym.crm.health;

import com.gym.crm.dao.jpa.TrainingTypeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingTypeHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainingTypeHealthIndicator.class);

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeHealthIndicator(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public Health health() {
        try {
            List<String> requiredTypes = List.of(
                    "STRENGTH",
                    "CARDIO",
                    "YOGA",
                    "PILATES",
                    "CROSSFIT"
            );

            long foundCount = requiredTypes.stream()
                    .filter(name -> trainingTypeDao.findByName(name).isPresent())
                    .count();

            if (foundCount == requiredTypes.size()) {
                return Health.up()
                        .withDetail("requiredTrainingTypes", requiredTypes.size())
                        .withDetail("foundTrainingTypes", foundCount)
                        .build();
            }

            return Health.outOfService()
                    .withDetail("requiredTrainingTypes", requiredTypes.size())
                    .withDetail("foundTrainingTypes", foundCount)
                    .withDetail("message", "One or more required training types are missing.")
                    .build();

        } catch (Exception exception) {
            LOGGER.error("Training type health check failed.", exception);

            return Health.down()
                    .withException(exception)
                    .build();
        }
    }
}