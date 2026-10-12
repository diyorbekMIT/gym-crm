package com.gym.crm.config;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StorageConfig.class)
class StorageConfigTest {

    @Test
    void shouldCreateSeparateStorageBeans(
            @Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage,
            @Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage,
            @Qualifier("trainingStorage") Map<Long, Training> trainingStorage
    ) {
        assertThat(traineeStorage).isEmpty();
        assertThat(trainerStorage).isEmpty();
        assertThat(trainingStorage).isEmpty();

        assertThat(traineeStorage).isNotSameAs(trainerStorage);
        assertThat(trainerStorage).isNotSameAs(trainingStorage);
    }
}