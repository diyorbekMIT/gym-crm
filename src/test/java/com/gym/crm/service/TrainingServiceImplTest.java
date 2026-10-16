package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.service.exception.InvalidTrainingException;
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

        trainingService = new TrainingServiceImpl(trainingDao, traineeDao, trainerDao);
    }

    // ==================== create (happy path) ====================

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

    // ==================== create (failure scenarios) ====================

    @Test
    void shouldRejectNullTrainingOnCreate() {
        assertThatThrownBy(() -> trainingService.create(null))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training must not be null.");
    }

    @Test
    void shouldRejectTrainingWithNullTraineeId() {
        Training training = validTraining();
        training.setTraineeId(null);

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training trainee ID must be a positive number.");
    }

    @Test
    void shouldRejectTrainingWithZeroTraineeId() {
        Training training = validTraining();
        training.setTraineeId(0L);

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training trainee ID must be a positive number.");
    }

    @Test
    void shouldRejectTrainingWithNullTrainerId() {
        Training training = validTraining();
        training.setTrainerId(null);

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training trainer ID must be a positive number.");
    }

    @Test
    void shouldRejectTrainingWithBlankName() {
        Training training = validTraining();
        training.setTrainingName("  ");

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training name must not be blank.");
    }

    @Test
    void shouldRejectTrainingWithNullType() {
        Training training = validTraining();
        training.setTrainingType(null);

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training type must not be null.");
    }

    @Test
    void shouldRejectTrainingWithNullDate() {
        Training training = validTraining();
        training.setTrainingDate(null);

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training date must not be null.");
    }

    @Test
    void shouldRejectTrainingWithZeroDuration() {
        Training training = validTraining();
        training.setTrainingDuration(0);

        assertThatThrownBy(() -> trainingService.create(training))
                .isInstanceOf(InvalidTrainingException.class)
                .hasMessage("Training duration must be a positive number of minutes.");
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

    // ==================== findById ====================

    @Test
    void shouldReturnTrainingWhenFoundById() {
        Training training = new Training();
        training.setId(5L);

        when(trainingDao.findById(5L)).thenReturn(Optional.of(training));

        Training result = trainingService.findById(5L);

        assertThat(result).isSameAs(training);
    }

    @Test
    void shouldRejectNullIdOnFindById() {
        assertThatThrownBy(() -> trainingService.findById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training ID must be a positive number.");
    }

    @Test
    void shouldRejectNegativeIdOnFindById() {
        assertThatThrownBy(() -> trainingService.findById(-5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training ID must be a positive number.");
    }

    @Test
    void shouldThrowEntityNotFoundWhenTrainingNotFound() {
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainingService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Training with id=99 was not found.");
    }

    // ==================== helpers ====================

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