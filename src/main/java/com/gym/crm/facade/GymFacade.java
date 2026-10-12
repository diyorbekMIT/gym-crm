package com.gym.crm.facade;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // ---------- Trainee operations ----------

    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.update(trainee);
    }

    public void deleteTraineeById(Long traineeId) {
        traineeService.deleteById(traineeId);
    }

    public Trainee getTraineeById(Long traineeId) {
        return traineeService.findById(traineeId);
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.findAll();
    }

    // ---------- Trainer operations ----------

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Trainer getTrainerById(Long trainerId) {
        return trainerService.findById(trainerId);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.findAll();
    }

    // ---------- Training operations ----------

    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Training getTrainingById(Long trainingId) {
        return trainingService.findById(trainingId);
    }

    public List<Training> getAllTrainings() {
        return trainingService.findAll();
    }
}