package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import com.gym.crm.service.exception.EntityNotFoundException;
import com.gym.crm.service.exception.InvalidTrainingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Training create(Training training) {
        validateTrainingForCreation(training);

        if (traineeDao.findById(training.getTraineeId()).isEmpty()) {
            LOGGER.warn(
                    "Cannot create training. Trainee id={} was not found.",
                    training.getTraineeId()
            );

            throw new EntityNotFoundException(
                    "Trainee with id=" + training.getTraineeId() + " was not found."
            );
        }

        if (trainerDao.findById(training.getTrainerId()).isEmpty()) {
            LOGGER.warn(
                    "Cannot create training. Trainer id={} was not found.",
                    training.getTrainerId()
            );

            throw new EntityNotFoundException(
                    "Trainer with id=" + training.getTrainerId() + " was not found."
            );
        }

        Training savedTraining = trainingDao.save(training);

        LOGGER.info(
                "Created training: id={}, traineeId={}, trainerId={}.",
                savedTraining.getId(),
                savedTraining.getTraineeId(),
                savedTraining.getTrainerId()
        );

        return savedTraining;
    }

    @Override
    public Training findById(Long id) {
        validateId(id);

        return trainingDao.findById(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Training with id={} was not found.", id);

                    return new EntityNotFoundException(
                            "Training with id=" + id + " was not found."
                    );
                });
    }

    @Override
    public List<Training> findAll() {
        List<Training> trainings = trainingDao.findAll();

        LOGGER.info("Selected {} training(s).", trainings.size());

        return trainings;
    }

    private void validateTrainingForCreation(Training training) {
        if (training == null) {
            throw new InvalidTrainingException("Training must not be null.");
        }

        if (training.getTraineeId() == null || training.getTraineeId() <= 0) {
            throw new InvalidTrainingException(
                    "Training trainee ID must be a positive number."
            );
        }

        if (training.getTrainerId() == null || training.getTrainerId() <= 0) {
            throw new InvalidTrainingException(
                    "Training trainer ID must be a positive number."
            );
        }

        if (training.getTrainingName() == null || training.getTrainingName().isBlank()) {
            throw new InvalidTrainingException(
                    "Training name must not be blank."
            );
        }

        if (training.getTrainingType() == null) {
            throw new InvalidTrainingException(
                    "Training type must not be null."
            );
        }

        if (training.getTrainingDate() == null) {
            throw new InvalidTrainingException(
                    "Training date must not be null."
            );
        }

        if (training.getTrainingDuration() == null
                || training.getTrainingDuration() <= 0) {

            throw new InvalidTrainingException(
                    "Training duration must be a positive number of minutes."
            );
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Training ID must be a positive number."
            );
        }
    }
}