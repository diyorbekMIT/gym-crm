package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainingServiceImplTest {

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        traineeDao = mock(TraineeDao.class);
        trainerDao = mock(TrainerDao.class);

        trainingService = new TrainingServiceImpl();
        trainingService.setTrainingDao(trainingDao);
        trainingService.setTraineeDao(traineeDao);
        trainingService.setTrainerDao(trainerDao);
    }

    @Test
    void shouldSaveTrainingWhenTraineeAndTrainerExist() {
        Training training = validTraining();

        when(traineeDao.findById(1L))
                .thenReturn(Optional.of(new Trainee()));

        when(trainerDao.findById(2L))
                .thenReturn(Optional.of(new Trainer()));

        when(trainingDao.save(training))
                .thenAnswer(invocation -> {
                    Training savedTraining = invocation.getArgument(0);
                    savedTraining.setId(10L);
                    return savedTraining;
                });

        Training result = trainingService.create(training);

        assertThat(result.getId()).isEqualTo(10L);

        verify(trainingDao).save(training);
    }

    @Test
    void shouldRejectTrainingWhenTraineeDoesNotExist() {
        Training training = validTraining();

        when(traineeDao.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainee with id=1 was not found.");
    }

    @Test
    void shouldRejectTrainingWhenTrainerDoesNotExist() {
        Training training = validTraining();

        when(traineeDao.findById(1L))
                .thenReturn(Optional.of(new Trainee()));

        when(trainerDao.findById(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainer with id=2 was not found.");
    }

    private Training validTraining() {
        Training training = new Training();
        training.setTraineeId(1L);
        training.setTrainerId(2L);
        training.setTrainingName("Morning Cardio");
        training.setTrainingType(new TrainingType("Cardio"));
        training.setTrainingDate(LocalDate.of(2026, 10, 12));
        training.setTrainingDuration(45);

        return training;
    }
}