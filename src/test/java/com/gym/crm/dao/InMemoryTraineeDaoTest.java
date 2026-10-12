package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTraineeDaoTest {

    private InMemoryTraineeDao traineeDao;
    private Map<Long, Trainee> traineeStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();

        Trainee existingTrainee = new Trainee();
        existingTrainee.setUserId(5L);
        existingTrainee.setFirstName("Existing");

        traineeStorage.put(5L, existingTrainee);

        traineeDao = new InMemoryTraineeDao();
        traineeDao.setTraineeStorage(traineeStorage);
    }

    @Test
    void shouldAssignIdAfterExistingMaximumId() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");
        trainee.setDateOfBirth(LocalDate.of(2000, 5, 10));

        Trainee savedTrainee = traineeDao.save(trainee);

        assertThat(savedTrainee.getUserId()).isEqualTo(6L);
        assertThat(traineeStorage).containsEntry(6L, savedTrainee);
    }

    @Test
    void shouldDeleteExistingTrainee() {
        boolean deleted = traineeDao.deleteById(5L);

        assertThat(deleted).isTrue();
        assertThat(traineeDao.findById(5L)).isEmpty();
    }

    @Test
    void shouldReturnFalseWhenDeletingUnknownTrainee() {
        boolean deleted = traineeDao.deleteById(999L);

        assertThat(deleted).isFalse();
    }
}