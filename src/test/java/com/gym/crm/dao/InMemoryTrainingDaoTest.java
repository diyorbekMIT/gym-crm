package com.gym.crm.dao;

import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTrainingDaoTest {

    private InMemoryTrainingDao trainingDao;
    private Map<Long, Training> trainingStorage;

    @BeforeEach
    void setUp() {
        trainingStorage = new HashMap<>();

        Training existingTraining = new Training();
        existingTraining.setId(8L);
        existingTraining.setTrainingName("Existing Session");

        trainingStorage.put(8L, existingTraining);

        trainingDao = new InMemoryTrainingDao();
        trainingDao.setTrainingStorage(trainingStorage);
    }

    @Test
    void shouldAssignIdAfterHighestExistingId() {
        Training training = new Training();
        training.setTraineeId(1L);
        training.setTrainerId(1L);
        training.setTrainingName("Cardio Session");
        training.setTrainingType(new TrainingType("Cardio"));
        training.setTrainingDate(LocalDate.of(2026, 10, 12));
        training.setTrainingDuration(45);

        Training savedTraining = trainingDao.save(training);

        assertThat(savedTraining.getId()).isEqualTo(9L);
        assertThat(trainingStorage).containsEntry(9L, savedTraining);
    }

    @Test
    void shouldFindExistingTrainingById() {
        assertThat(trainingDao.findById(8L))
                .isPresent()
                .containsSame(trainingStorage.get(8L));
    }

    @Test
    void shouldReturnAllTrainingsSortedById() {
        Training secondTraining = new Training();
        secondTraining.setId(2L);

        trainingStorage.put(2L, secondTraining);

        assertThat(trainingDao.findAll())
                .extracting(Training::getId)
                .containsExactly(2L, 8L);
    }
}