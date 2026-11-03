package com.gym.crm.health;

import com.gym.crm.dao.jpa.TraineeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TraineeDataHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TraineeDataHealthIndicator.class);

    private final TraineeDao traineeDao;

    public TraineeDataHealthIndicator(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Health health() {
        try {
            long count = traineeDao.count();

            return Health.up()
                    .withDetail("traineeCount", count)
                    .build();

        } catch (Exception exception) {
            LOGGER.error("Trainee data health check failed.", exception);

            return Health.down()
                    .withException(exception)
                    .build();
        }
    }
}