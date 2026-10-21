package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.dao.jpa.TrainingDao;
import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.service.exception.AuthenticationException;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TrainingManagementServiceTest {

    private static final LocalDate TRAINING_DATE =
            LocalDate.of(2026, 10, 21);

    private AuthenticationService authenticationService;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;
    private TrainingDao trainingDao;

    private TrainingManagementService trainingService;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        trainerDao = mock(TrainerDao.class);
        trainingTypeDao = mock(TrainingTypeDao.class);
        trainingDao = mock(TrainingDao.class);

        trainingService = new TrainingManagementService(
                authenticationService,
                trainerDao,
                trainingTypeDao,
                trainingDao
        );
    }

    @Test
    void shouldSaveTrainingForAuthenticatedTrainee() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType trainingType = mock(TrainingType.class);

        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(trainee);

        when(trainerDao.findById(2L))
                .thenReturn(Optional.of(trainer));

        when(trainingTypeDao.findById(3L))
                .thenReturn(Optional.of(trainingType));

        when(trainingDao.save(any(Training.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Training result = addTraining(45);

        assertThat(result.getTrainee()).isSameAs(trainee);
        assertThat(result.getTrainer()).isSameAs(trainer);
        assertThat(result.getTrainingType()).isSameAs(trainingType);

        assertThat(result.getTrainingName())
                .isEqualTo("Morning Cardio");

        assertThat(result.getTrainingDate())
                .isEqualTo(TRAINING_DATE);

        assertThat(result.getTrainingDuration()).isEqualTo(45);
    }

    @Test
    void shouldRejectInvalidCredentialsBeforeAccessingTrainingData() {
        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenThrow(new AuthenticationException());

        assertThatThrownBy(() -> addTraining(45))
                .isInstanceOf(AuthenticationException.class);

        verifyNoInteractions(
                trainerDao,
                trainingTypeDao,
                trainingDao
        );
    }

    @Test
    void shouldRejectZeroDurationBeforeLookingUpReferences() {
        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(new Trainee());

        assertThatThrownBy(() -> addTraining(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Training duration must be a positive number of minutes."
                );

        verifyNoInteractions(
                trainerDao,
                trainingTypeDao,
                trainingDao
        );
    }

    @Test
    void shouldRejectUnknownTrainerWithoutSavingTraining() {
        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(new Trainee());

        when(trainerDao.findById(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> addTraining(45))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Trainer with id=2 was not found.");

        verifyNoInteractions(trainingTypeDao, trainingDao);
    }

    @Test
    void shouldRejectUnknownTrainingTypeWithoutSavingTraining() {
        when(authenticationService.authenticateTrainee(
                "John.Smith",
                "FakePass12"
        )).thenReturn(new Trainee());

        when(trainerDao.findById(2L))
                .thenReturn(Optional.of(new Trainer()));

        when(trainingTypeDao.findById(3L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> addTraining(45))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Training type with id=3 was not found.");

        verifyNoInteractions(trainingDao);
    }

    private Training addTraining(Integer durationMinutes) {
        return trainingService.addTraining(
                "John.Smith",
                "FakePass12",
                2L,
                "Morning Cardio",
                3L,
                TRAINING_DATE,
                durationMinutes
        );
    }
}