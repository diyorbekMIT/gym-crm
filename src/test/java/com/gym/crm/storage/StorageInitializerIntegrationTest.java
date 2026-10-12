package com.gym.crm.storage;

import com.gym.crm.config.AppConfig;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(AppConfig.class)
class StorageInitializerIntegrationTest {

    @Autowired
    @Qualifier("traineeStorage")
    private Map<Long, Trainee> traineeStorage;

    @Autowired
    @Qualifier("trainerStorage")
    private Map<Long, Trainer> trainerStorage;

    @Autowired
    @Qualifier("trainingStorage")
    private Map<Long, Training> trainingStorage;

    @Test
    void shouldLoadPreparedDataIntoStorageMaps() {
        assertThat(traineeStorage)
                .hasSize(2)
                .containsKey(1L)
                .containsKey(2L);

        assertThat(trainerStorage)
                .hasSize(2)
                .containsKey(1L)
                .containsKey(2L);

        assertThat(trainingStorage)
                .hasSize(2)
                .containsKey(1L)
                .containsKey(2L);
    }
}