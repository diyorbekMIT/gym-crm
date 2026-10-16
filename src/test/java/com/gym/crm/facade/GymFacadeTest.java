package com.gym.crm.facade;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    // ==================== Trainee ====================

    @Test
    void shouldDelegateTraineeCreationToTraineeService() {
        Trainee trainee = new Trainee();

        when(traineeService.create(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee(trainee);

        assertThat(result).isSameAs(trainee);
        verify(traineeService).create(trainee);
    }

    @Test
    void shouldDelegateTraineeUpdateToTraineeService() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);

        when(traineeService.update(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.updateTrainee(trainee);

        assertThat(result).isSameAs(trainee);
        verify(traineeService).update(trainee);
    }

    @Test
    void shouldDelegateTraineeDeletionToTraineeService() {
        gymFacade.deleteTraineeById(5L);

        verify(traineeService).deleteById(5L);
    }

    @Test
    void shouldDelegateGetTraineeByIdToTraineeService() {
        Trainee trainee = new Trainee();
        trainee.setUserId(1L);

        when(traineeService.findById(1L)).thenReturn(trainee);

        Trainee result = gymFacade.getTraineeById(1L);

        assertThat(result).isSameAs(trainee);
        verify(traineeService).findById(1L);
    }

    @Test
    void shouldDelegateGetAllTraineesToTraineeService() {
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());

        when(traineeService.findAll()).thenReturn(trainees);

        List<Trainee> result = gymFacade.getAllTrainees();

        assertThat(result).isSameAs(trainees);
        verify(traineeService).findAll();
    }

    // ==================== Trainer ====================

    @Test
    void shouldDelegateTrainerCreationToTrainerService() {
        Trainer trainer = new Trainer();

        when(trainerService.create(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer(trainer);

        assertThat(result).isSameAs(trainer);
        verify(trainerService).create(trainer);
    }

    @Test
    void shouldDelegateTrainerUpdateToTrainerService() {
        Trainer trainer = new Trainer();
        trainer.setUserId(1L);

        when(trainerService.update(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.updateTrainer(trainer);

        assertThat(result).isSameAs(trainer);
        verify(trainerService).update(trainer);
    }

    @Test
    void shouldDelegateGetTrainerByIdToTrainerService() {
        Trainer trainer = new Trainer();
        trainer.setUserId(2L);

        when(trainerService.findById(2L)).thenReturn(trainer);

        Trainer result = gymFacade.getTrainerById(2L);

        assertThat(result).isSameAs(trainer);
        verify(trainerService).findById(2L);
    }

    @Test
    void shouldDelegateGetAllTrainersToTrainerService() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());

        when(trainerService.findAll()).thenReturn(trainers);

        List<Trainer> result = gymFacade.getAllTrainers();

        assertThat(result).isSameAs(trainers);
        verify(trainerService).findAll();
    }

    // ==================== Training ====================

    @Test
    void shouldDelegateTrainingCreationToTrainingService() {
        Training training = new Training();

        when(trainingService.create(training)).thenReturn(training);

        Training result = gymFacade.createTraining(training);

        assertThat(result).isSameAs(training);
        verify(trainingService).create(training);
    }

    @Test
    void shouldDelegateGetTrainingByIdToTrainingService() {
        Training training = new Training();
        training.setId(3L);

        when(trainingService.findById(3L)).thenReturn(training);

        Training result = gymFacade.getTrainingById(3L);

        assertThat(result).isSameAs(training);
        verify(trainingService).findById(3L);
    }

    @Test
    void shouldDelegateGetAllTrainingsToTrainingService() {
        List<Training> trainings = List.of(new Training(), new Training());

        when(trainingService.findAll()).thenReturn(trainings);

        List<Training> result = gymFacade.getAllTrainings();

        assertThat(result).isSameAs(trainings);
        verify(trainingService).findAll();
    }
}