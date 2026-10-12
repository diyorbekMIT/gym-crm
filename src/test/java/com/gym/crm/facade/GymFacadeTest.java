package com.gym.crm.facade;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GymFacadeTest {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;
    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        trainingService = mock(TrainingService.class);

        gymFacade = new GymFacade(
                traineeService,
                trainerService,
                trainingService
        );
    }

    @Test
    void shouldDelegateTraineeCreationToTraineeService() {
        Trainee trainee = new Trainee();

        when(traineeService.create(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee(trainee);

        assertThat(result).isSameAs(trainee);
        verify(traineeService).create(trainee);
    }

    @Test
    void shouldDelegateTrainerCreationToTrainerService() {
        Trainer trainer = new Trainer();

        when(trainerService.create(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer(trainer);

        assertThat(result).isSameAs(trainer);
        verify(trainerService).create(trainer);
    }

    @Test
    void shouldDelegateTrainingCreationToTrainingService() {
        Training training = new Training();

        when(trainingService.create(training)).thenReturn(training);

        Training result = gymFacade.createTraining(training);

        assertThat(result).isSameAs(training);
        verify(trainingService).create(training);
    }

    @Test
    void shouldDelegateTraineeDeletionToTraineeService() {
        gymFacade.deleteTraineeById(5L);

        verify(traineeService).deleteById(5L);
    }
}