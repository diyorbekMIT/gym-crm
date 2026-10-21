package com.gym.crm.service.jpa;

import com.gym.crm.dao.jpa.TrainerDao;
import com.gym.crm.dao.jpa.TrainingDao;
import com.gym.crm.dao.jpa.TrainingTypeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.service.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TrainingManagementService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TrainingManagementService.class);

    private final AuthenticationService authenticationService;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingDao trainingDao;

    public TrainingManagementService(
            AuthenticationService authenticationService,
            TrainerDao trainerDao,
            TrainingTypeDao trainingTypeDao,
            TrainingDao trainingDao
    ) {
        this.authenticationService = authenticationService;
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.trainingDao = trainingDao;
    }

    @Transactional
    public Training addTraining(
            String traineeUsername,
            String traineePassword,
            Long trainerId,
            String trainingName,
            Long trainingTypeId,
            LocalDate trainingDate,
            Integer durationMinutes
    ) {
        Trainee trainee = authenticationService.authenticateTrainee(
                traineeUsername,
                traineePassword
        );

        validateTraining(
                trainerId,
                trainingName,
                trainingTypeId,
                trainingDate,
                durationMinutes
        );

        Trainer trainer = trainerDao.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trainer with id=" + trainerId + " was not found."
                ));

        TrainingType trainingType = trainingTypeDao
                .findById(trainingTypeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Training type with id="
                                + trainingTypeId
                                + " was not found."
                ));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingName.trim());
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(durationMinutes);

        Training savedTraining = trainingDao.save(training);

        LOGGER.info(
                "Added training: id={}, traineeId={}, trainerId={}.",
                savedTraining.getId(),
                trainee.getId(),
                trainer.getId()
        );

        return savedTraining;
    }

    private void validateTraining(
            Long trainerId,
            String trainingName,
            Long trainingTypeId,
            LocalDate trainingDate,
            Integer durationMinutes
    ) {
        if (trainerId == null || trainerId <= 0) {
            throw new IllegalArgumentException(
                    "Trainer ID must be a positive number."
            );
        }

        if (trainingName == null || trainingName.isBlank()) {
            throw new IllegalArgumentException(
                    "Training name must not be blank."
            );
        }

        if (trainingTypeId == null || trainingTypeId <= 0) {
            throw new IllegalArgumentException(
                    "Training type ID must be a positive number."
            );
        }

        if (trainingDate == null) {
            throw new IllegalArgumentException(
                    "Training date must not be null."
            );
        }

        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Training duration must be a positive number of minutes."
            );
        }
    }
}