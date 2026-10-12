package com.gym.crm.dao;

import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryTrainerDaoTest {

    private InMemoryTrainerDao trainerDao;
    private Map<Long, Trainer> trainerStorage;

    @BeforeEach
    void setUp() {
        trainerStorage = new HashMap<>();

        Trainer existingTrainer = new Trainer();
        existingTrainer.setUserId(4L);
        existingTrainer.setUsername("Existing.Trainer");
        existingTrainer.setSpecialization(new TrainingType("Strength"));

        trainerStorage.put(4L, existingTrainer);

        trainerDao = new InMemoryTrainerDao();
        trainerDao.setTrainerStorage(trainerStorage);
    }

    @Test
    void shouldAssignIdAfterHighestExistingId() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sarah");
        trainer.setLastName("Davis");
        trainer.setSpecialization(new TrainingType("Cardio"));

        Trainer savedTrainer = trainerDao.save(trainer);

        assertThat(savedTrainer.getUserId()).isEqualTo(5L);
        assertThat(trainerStorage).containsEntry(5L, savedTrainer);
    }

    @Test
    void shouldFindTrainerByUsername() {
        assertThat(trainerDao.findByUsername("Existing.Trainer"))
                .isPresent()
                .containsSame(trainerStorage.get(4L));
    }

    @Test
    void shouldUpdateExistingTrainer() {
        Trainer trainer = trainerStorage.get(4L);
        trainer.setFirstName("Updated");

        Trainer updatedTrainer = trainerDao.update(trainer);

        assertThat(updatedTrainer.getFirstName()).isEqualTo("Updated");
        assertThat(trainerStorage.get(4L).getFirstName()).isEqualTo("Updated");
    }

    @Test
    void shouldRejectUpdateForUnknownTrainer() {
        Trainer unknownTrainer = new Trainer();
        unknownTrainer.setUserId(999L);

        assertThatThrownBy(() -> trainerDao.update(unknownTrainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not exist");
    }
}