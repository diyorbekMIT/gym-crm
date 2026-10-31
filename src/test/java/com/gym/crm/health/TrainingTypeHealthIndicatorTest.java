package com.gym.crm.health;

import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeHealthIndicatorTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypeHealthIndicator healthIndicator;

    @Test
    void shouldReturnUpWhenAllTrainingTypesExist() {
        TrainingType trainingType = new TrainingType();

        when(trainingTypeDao.findByName(anyString()))
                .thenReturn(Optional.of(trainingType));

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void shouldReturnOutOfServiceWhenTrainingTypeIsMissing() {
        when(trainingTypeDao.findByName(anyString()))
                .thenReturn(Optional.empty());

        Health health = healthIndicator.health();

        assertThat(health.getStatus())
                .isEqualTo(Status.OUT_OF_SERVICE);
    }
}